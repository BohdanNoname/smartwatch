package com.nedash.com.smartwatch.notifier.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.nedash.com.smartwatch.notifier.app.utils.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.*

@HiltAndroidApp
class App: Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var appOpenAdManager: AppOpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(this)
//        val bd = AppDatabase.provideDatabase(this)
//        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
//            override fun onStart(owner: LifecycleOwner) {
//                super.onStart(owner)
//                checkUninstalledApps(bd.appDao())
//                currentActivity?.let {
//                    if (!(it as MainActivity).isPro)
//                        appOpenAdManager.showAdIfAvailable(it)
//                }
//            }
//        })
        appOpenAdManager = AppOpenAdManager()
        sharedPrefs = SharedPreferences(baseContext)
//        checkModesInsert(bd.modeDao())
    }

//    private fun checkUninstalledApps(appDao: AppDao) {
//        applicationScope.launch {
//            val addedApps = appDao.getAllApps()
//            val installedApps =
//                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//                    .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
//            appDao.deleteApps(addedApps.filter { app -> !installedApps.any { it.packageName == app.appPackage } })
//        }
//    }

//    private fun checkModesInsert(modeDao: ModeDao) {
//        applicationScope.launch {
//            if (!sharedPrefs.getBool(SharedPrefs.MODES_FIRST_INSERT)) {
//                modeDao.insertModes(
//                    ModeEntity(
//                        0,
//                        getString(R.string.title_name_offline_play),
//                        1,
//                        bluetooth = false,
//                        brightness = 100,
//                        screenRotation = true,
//                        autoSync = false,
//                        sound = 1
//                    ),
//                    ModeEntity(
//                        0,
//                        getString(R.string.title_name_power_play),
//                        2,
//                        bluetooth = false,
//                        brightness = 100,
//                        screenRotation = true,
//                        autoSync = true,
//                        sound = 1
//                    ),
//                    ModeEntity(
//                        0,
//                        getString(R.string.title_name_long_play),
//                        3,
//                        bluetooth = false,
//                        brightness = 50,
//                        screenRotation = true,
//                        autoSync = false,
//                        sound = 0
//                    )
//                )
//                sharedPrefs.putValue(SharedPrefs.MODES_FIRST_INSERT, true)
//            }
//        }
//    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete
     * (i.e. dismissed or fails to show).
     */
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    /** Inner class that loads and shows app open ads. */
    private inner class AppOpenAdManager {

        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
        private var loadTime: Long = 0

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        fun loadAd(context: Context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                BuildConfig.OPEN_ADD,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                    }
                })
        }

        /** Check if ad was loaded more than n hours ago. */
        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        /** Check if ad exists and can be shown. */
        private fun isAdAvailable(): Boolean {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(
                activity,
                object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                })
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener
        ) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                return
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
                return
            }

            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                /** Called when full screen content is dismissed. */
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                /** Called when fullscreen content failed to show. */
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                }

                /** Called when fullscreen content is shown. */
                override fun onAdShowedFullScreenContent() {}
            }
            isShowingAd = true
            appOpenAd?.show(activity)
        }
    }
}