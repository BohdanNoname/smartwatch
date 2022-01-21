package com.nedash.com.smartwatch.notifier.app.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nedash.com.smartwatch.notifier.app.BuildConfig


class Interstitial constructor(private val context: Context) {

    init {
        loadAd()
    }

    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false

    private fun loadAd() {
        InterstitialAd.load(
            context, BuildConfig.INTER_ADD, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    mAdIsLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                }
            }
        )
    }

    // Show the ad if it's ready. Otherwise toast and restart the game.
    fun showInterstitial(activity: Activity, isPro: Boolean,  onAddDismiss: () -> Unit) {
        if (mInterstitialAd != null && !isPro) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    onAddDismiss.invoke()
                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(activity)
        } else
            onAddDismiss.invoke()
    }
}