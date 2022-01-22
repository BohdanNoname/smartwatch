package com.nedash.com.smartwatch.notifier.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.android.billingclient.api.SkuDetails
import com.nedash.com.smartwatch.notifier.app.BuildConfig
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.DialogChangeDefaultThemeBinding
import com.nedash.com.smartwatch.notifier.app.databinding.DialogMuteTimerBinding
import com.nedash.com.smartwatch.notifier.app.db.DataBaseSmartWatch
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import com.nedash.com.smartwatch.notifier.app.ui.activity.MainActivity
import com.nedash.com.smartwatch.notifier.app.utils.Dialogs.showTimerDialog
import com.nedash.com.smartwatch.notifier.app.utils.Utils.changeMainTheme
import com.nedash.com.smartwatch.notifier.app.utils.Utils.gone
import com.nedash.com.smartwatch.notifier.app.utils.Utils.mainTheme
import com.nedash.com.smartwatch.notifier.app.utils.Utils.showToast
import com.nedash.com.smartwatch.notifier.app.utils.Utils.visible
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object Utils {
    fun View.gone() {
        visibility = View.GONE
    }

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun View.enable() {
        isEnabled = true
    }

    fun View.disable() {
        isEnabled = false
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun NavController.navigateWithSlideAnimation(@IdRes resId: Int) {
        val navOptions = NavOptions.Builder().apply {
            setEnterAnim(R.anim.slide_in_right)
            setExitAnim(R.anim.slide_out_left)
            setPopEnterAnim(R.anim.slide_in_left)
            setPopExitAnim(R.anim.slide_out_right)
        }
        navigate(resId, null, navOptions.build())
    }

    fun NavController.navToPurchaseInsideFragment() {
        val navOptions = NavOptions.Builder().apply {
            setEnterAnim(android.R.anim.fade_in)
            setExitAnim(android.R.anim.fade_out)
        }
        navigate(R.id.purchaseInsideApplicationFragment, null, navOptions.build())
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Fragment.getColoredText(@StringRes id: Int, vararg args: Any?): CharSequence =
        HtmlCompat.fromHtml(String.format(getString(id), *args), HtmlCompat.FROM_HTML_MODE_COMPACT)


    fun Context.showToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()


    fun Context.checkPermissions(): Boolean {
        val n =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            Settings.System.canWrite(this) && n.isNotificationPolicyAccessGranted
        else true
    }

    fun Context.appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return false
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return true
    }

    fun SharedPreferences.mainTheme(): Int{
        return when(getInt(SharedPreferences.MAIN_THEME_KEY)){
            0 -> R.style.Theme_Black
            1 -> R.style.Theme_Blue
            2 -> R.style.Theme_Green
            else -> R.style.Theme_Black
        }
    }

    fun SharedPreferences.changeMainTheme(@IdRes themeId: Int){
        val value = when(themeId){
            R.style.Theme_Black -> 0
            R.style.Theme_Blue -> 1
            R.style.Theme_Green -> 2
            else -> 0
        }
        putValue(SharedPreferences.MAIN_THEME_KEY, value)
    }

    fun Context.openURL(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            })
        } catch (e: Exception) {
            Toast.makeText(this, R.string.no_application_found, Toast.LENGTH_SHORT).show()
        }
    }

    fun Context.shareApp() {
        try {
            startActivity(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                )
            })
        } catch (e: Exception) {
            Toast.makeText(this, R.string.no_application_found, Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun getAllApps(context: Context, dataBase: DataBaseSmartWatch): List<AppDataEntity> {
        val pm = context.packageManager
        val addedApps = dataBase.daoAppData().getAll()

        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter {
                it.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
            .filter { app ->
                !addedApps.any { it.applicationName == app.packageName }
            }
            .map { appInfo ->
                AppDataEntity(
                    icon = appInfo.icon,
                    applicationName = appInfo.loadLabel(pm).toString(),
                    packageName = appInfo.packageName,
                )
            }
    }

    fun SkuDetails.convertPrice(): String {
        val price = priceAmountMicros / 1000000.0
        return getText(price.toString())
    }

    fun SkuDetails.calculateOldPrice(discount: Int): String {
        with(DecimalFormat("#.##")) {
            decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH)
            val price =
                format(priceAmountMicros / 1000000.0 * 100 / (100 - discount))
            return getText(price)
        }
    }

    fun SkuDetails.calculatePeriodPrice(period: Int): String {
        with(DecimalFormat("#.##")) {
            decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH)
            val price = format(priceAmountMicros / 1000000.0 / period)
            return getText(price)
        }
    }

    private fun SkuDetails.getText(price: String): String =
        if (priceCurrencyCode.length > 1) "$price $priceCurrencyCode" else priceCurrencyCode + price
}

object Dialogs{


    fun Fragment.showTimerDialog(
        context: Context,
        layoutInflater: LayoutInflater
    ): Dialog {
        val binding = DialogMuteTimerBinding.inflate(layoutInflater)
        with(Dialog(context)){
            with(binding){

            }
            show()
            return this
        }
    }



    @SuppressLint("ResourceType")
    fun Fragment.showChangeDefaultThemeDialog(
        context: Context,
        layoutInflater: LayoutInflater
    ): Dialog{

        val binding = DialogChangeDefaultThemeBinding.inflate(layoutInflater)
        with(Dialog(context)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(binding.root)
            with(binding) {
                val sharedPreferences = SharedPreferences(context)
                if((ownerActivity as MainActivity).isPro){
                    ivLockBlue.gone()
                    ivLockGreen.gone()

                    when(sharedPreferences.mainTheme()){
                        0 -> radioButtonBlack.isChecked = true
                        1 -> radioButtonBlue.isChecked = true
                        2 -> radioButtonGreen.isChecked = true
                    }

                    clBlackTheme
                        .changeDefaultTheme(
                            this,
                            sharedPreferences,
                            (activity as MainActivity))
                    clBlueTheme
                        .changeDefaultTheme(
                            this,
                            sharedPreferences,
                            (activity as MainActivity))
                    clGreenTheme
                        .changeDefaultTheme(this,
                            sharedPreferences,
                            (activity as MainActivity))
                } else {
                    ivLockBlue.visible()
                    ivLockGreen.visible()

                    with(context){
                        clBlackTheme.setOnClickListener {
                            showToast(getString(R.string.buy_app_message_toast))
                        }
                        clBlueTheme.setOnClickListener {
                            showToast(getString(R.string.buy_app_message_toast))
                        }
                        clGreenTheme.setOnClickListener {
                            showToast(getString(R.string.buy_app_message_toast))
                        }
                    }
                }
                ivClose.setOnClickListener { dismiss() }
            }
            show()
            return this
        }
    }

    private fun View.changeDefaultTheme(
        binding: DialogChangeDefaultThemeBinding,
        sharedPreferences: SharedPreferences,
        activity: MainActivity) {
        with(binding){
            val newMainTheme = when(id){
                R.id.cl_black_theme -> {
                    radioButtonBlack.isChecked = true
                    R.style.Theme_Black
                }
                R.id.cl_blue_theme -> {
                    radioButtonBlue.isChecked = true
                    R.style.Theme_Blue
                }
                R.id.cl_green_theme -> {
                    radioButtonGreen.isChecked = true
                    R.style.Theme_Green
                }
                else -> {
                    radioButtonBlack.isChecked = true
                    R.style.Theme_Black
                }
            }
            sharedPreferences.changeMainTheme(newMainTheme)
            with(context){
                showToast(getString(R.string.activity_will_restart))
            }
        }
    }
}

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}