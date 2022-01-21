package com.nedash.com.smartwatch.notifier.app.ui.activity

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.ColorRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import com.nedash.com.smartwatch.notifier.app.BuildConfig
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.billing.BillingHelper
import com.nedash.com.smartwatch.notifier.app.databinding.ActivityMainBinding
import com.nedash.com.smartwatch.notifier.app.utils.SharedPreferences
import com.nedash.com.smartwatch.notifier.app.utils.Utils.gone
import com.nedash.com.smartwatch.notifier.app.utils.Utils.mainTheme
import com.nedash.com.smartwatch.notifier.app.utils.Utils.showToast
import com.nedash.com.smartwatch.notifier.app.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var billingHelper: BillingHelper
    private lateinit var adView: AdView
    var isPro: Boolean = false

    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.includeAd.flAdBanner.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPreferences = SharedPreferences(this)
        val mainTheme = sharedPreferences.mainTheme()
        setTheme(mainTheme)

        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.container_fragments)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.appsSettingsFragment, R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        billingHelper = BillingHelper(this)
        with(billingHelper) {
            init()
            isPro.observe(this@MainActivity, {
                this@MainActivity.isPro = it ?: false
                with(binding) {
                    if (this@MainActivity.isPro) {
                        with(includeAd) {
                            llGetProBanner.gone()
                            flAdBanner.gone()
                        }
                    } else {
                        with(includeAd) {
                            llGetProBanner.visible()
                            flAdBanner.visible()
                        }
                    }
                }
            })
            skuQueryError.observe(this@MainActivity, {
                it?.getContentIfNotHandled().let { error ->
                    error?.let {
                        showToast(getString(R.string.some_error_occurred))
                    }
                }
            })
        }
        showBannerOrPro()
    }

    private val listener =
        NavController.OnDestinationChangedListener{ _, destination, _ ->
            when(destination.id){
//                TODO(SHOW OR HIDE ICONS IN TOOLBAR)
                R.id.homeFragment -> {}
                R.id.appsSettingsFragment -> {}
                R.id.settingsFragment -> {}
            }
        }

    private fun showOrHide(show: Boolean, @ColorRes color: Int, lock: Boolean = true, toolbarIcons: Boolean = true) {
        window?.statusBarColor = ContextCompat.getColor(this, color)
        with(binding) {
            if (show) {
                toolbar.visible()
                if (!isPro){
                    includeAd.rlAdContainer.visible()
                    toolbar.visible()
//                    TODO(Add here state of visibility for icons in toolbar)
                }
            } else {
                toolbar.gone()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.container_fragments)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        findNavController(R.id.container_fragments).addOnDestinationChangedListener(
            listener
        )
    }

    override fun onBackPressed() {
//        TODO(When fragment is open need to set return here)
        when (findNavController(R.id.container_fragments).currentDestination?.id) {
            R.id.purchaseBeforeTutorialFragment,
            R.id.purchaseAfterTutorialFragment,
            R.id.purchaseOfferFragment -> return
            else -> super.onBackPressed()
        }
    }

    private fun showBannerOrPro() {
        adView = AdView(this)
        with(binding) {
            includeAd.flAdBanner.addView(adView)

            adView.adUnitId = BuildConfig.BANNER_ADD

            adView.adSize = adSize

            val adRequest = AdRequest.Builder().build()

            // Start loading the ad in the background.
            adView.loadAd(adRequest)
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    includeAd.llGetProBanner.gone()
                    includeAd.flAdBanner.visible()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    includeAd.llGetProBanner.visible()
                    includeAd.flAdBanner.gone()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        findNavController(R.id.container_fragments)
            .removeOnDestinationChangedListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        billingHelper.onDestroy()
    }
}