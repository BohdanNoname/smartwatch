package com.nedash.com.smartwatch.notifier.app.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.nedash.com.smartwatch.notifier.app.utils.Event
import kotlinx.coroutines.flow.*


class BillingHelper constructor(context: Context) : LifecycleObserver, PurchasesUpdatedListener,
    BillingClientStateListener {

    private val _skuQueryError = MutableLiveData<Event<String>?>(null)
    val skuQueryError: LiveData<Event<String>?> = _skuQueryError

    private var _knownSubscriptionSKUs = arrayListOf<SkuDetails>()

    private var _isReady = MutableLiveData(false)
    val isReady: LiveData<Boolean> = _isReady

    private var _isPro = MutableLiveData<Boolean>(null)
    val isPro: LiveData<Boolean?> = _isPro

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val TAG = "BillingLifecycle => "

    fun init() {
        Log.d(TAG, "ON_CREATE")
        if (!billingClient.isReady) {
            Log.d(TAG, "BillingClient: Start connection...")
            billingClient.startConnection(this)
        }
    }

    fun onDestroy() {
        if (billingClient.isReady) {
            Log.d(TAG, "BillingClient can only be used once -- closing connection")
            billingClient.endConnection()
        }
    }

    override fun onBillingSetupFinished(p0: BillingResult) {
        val responseCode = p0.responseCode
        val debugMessage = p0.debugMessage
        _isReady.postValue(true)
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        queryPurchases()
    }

    override fun onBillingServiceDisconnected() {
        Log.d(TAG, "onBillingServiceDisconnected")
        _isReady.postValue(false)
        if (!billingClient.isReady)
            billingClient.startConnection(this)
    }

    fun querySubSku(skus: List<String>, onGetSubs: (List<SkuDetails>) -> Unit) {
        val knownSkus = _knownSubscriptionSKUs.map { it.sku }
        if (skus.any { it !in knownSkus })
            querySkuDetails(skus, onGetSubs)
        else
            onGetSubs.invoke(_knownSubscriptionSKUs.filter { it.sku in skus })
    }

    /**
     * In order to make purchasese, you need the [SkuDetails] for the item or subscription.
     * This is an asynchronous call that will receive a result in [onSkuDetailsResponse].
     */
    private fun querySkuDetails(
        listOfSkus: List<String>,
        onGetSubs: (List<SkuDetails>) -> Unit
    ) {
        Log.d(TAG, "querySkuDetails")
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.SUBS)
            .setSkusList(listOfSkus)
            .build()
        params.let { skuDetailsParams ->
            Log.i(TAG, "querySkuDetailsAsync")
            billingClient.querySkuDetailsAsync(skuDetailsParams) { p0, p1 ->
                when (p0.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        if (p1 == null)
                            _skuQueryError.value = Event("Error")
                        else {
                            _knownSubscriptionSKUs = (p1 + _knownSubscriptionSKUs).distinctBy { it.sku } as ArrayList<SkuDetails>
                            onGetSubs.invoke(p1)
                        }
                    }
                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
                    BillingClient.BillingResponseCode.DEVELOPER_ERROR,
                    BillingClient.BillingResponseCode.ERROR -> {
                        _skuQueryError.value = Event("Error")
                    }
                    BillingClient.BillingResponseCode.USER_CANCELED,
                    BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
                    BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                        // These response codes are not expected.
                        _skuQueryError.value = Event("Error")
                    }
                }
            }
        }
    }

    /**
     * Query Google Play Billing for existing purchases.
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    private fun queryPurchases() {
        if (!billingClient.isReady) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready")
        }
        Log.d(TAG, "queryPurchases: SUBS")
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { _, p1 ->
            for (purchase in p1) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED)
                    _isPro.postValue(true)
            }
        }
    }

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        val responseCode = p0.responseCode
        val debugMessage = p0.debugMessage
        Log.d(TAG, "onPurchasesUpdated: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (p1 == null) {
                    Log.d(TAG, "onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else
                    processPurchases(p1)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.i(TAG, "onPurchasesUpdated: User canceled the purchase")
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.i(TAG, "onPurchasesUpdated: The user already owns this item")
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Log.e(
                    TAG, "onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The SKU product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            }
        }
    }

    /**
     * Send purchase SingleLiveEvent and update purchases LiveData.
     *
     * The SingleLiveEvent will trigger network call to verify the subscriptions on the sever.
     * The LiveData will allow Google Play settings UI to update based on the latest purchase data.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        Log.d(TAG, "processPurchases: ${purchasesList?.size} purchase(s)")
        purchasesList?.let {
            logAcknowledgementStatus(purchasesList)
        }
    }

    /**
     * Log the number of purchases that are acknowledge and not acknowledged.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * When the purchase is first received, it will not be acknowledge.
     * This application sends the purchase token to the server for registration. After the
     * purchase token is registered to an account, the Android app acknowledges the purchase token.
     * The next time the purchase list is updated, it will contain acknowledged purchases.
     */
    private fun logAcknowledgementStatus(purchasesList: List<Purchase>) {
        for (purchase in purchasesList) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }
        }
    }

    /**
     * Launching the billing flow.
     *
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    fun launchBillingFlow(activity: Activity, skuDetails: SkuDetails): Int {
        if (!billingClient.isReady) {
            Log.e(TAG, "launchBillingFlow: BillingClient is not ready")
        }
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        val billingResult = billingClient.launchBillingFlow(activity, flowParams)
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "launchBillingFlow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    /**
     * Acknowledge a purchase.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * Apps should acknowledge the purchase after confirming that the purchase token
     * has been associated with a user. This app only acknowledges purchases after
     * successfully receiving the subscription data back from the server.
     *
     * Developers can choose to acknowledge purchases from a server using the
     * Google Play Developer API. The server has direct access to the user database,
     * so using the Google Play Developer API for acknowledgement might be more reliable.
     * TODO(134506821): Acknowledge purchases on the server.
     *
     * If the purchase token is not acknowledged within 3 days,
     * then Google Play will automatically refund and revoke the purchase.
     * This behavior helps ensure that users are not charged for subscriptions unless the
     * user has successfully received access to the content.
     * This eliminates a category of issues where users complain to developers
     * that they paid for something that the app is not giving to them.
     */
    private fun acknowledgePurchase(purchase: Purchase) {
        Log.d(TAG, "acknowledgePurchase")
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            _isPro.postValue(true)
            Log.d(TAG, "acknowledgePurchase: $responseCode $debugMessage")
        }
    }
}