package com.nedash.com.smartwatch.notifier.app.utils.seald_classes

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.nedash.com.smartwatch.notifier.app.R

sealed class NotificationTheme(
    @StringRes val name: Int,
    @ColorRes val color: Int,
    val isPro: Boolean = true
) {
    object White: NotificationTheme(name = R.string.color_name_white, color = R.color.color_name_white, isPro = false)
    object RichBlack: NotificationTheme(name = R.string.color_name_rich_black, color = R.color.color_name_rich_black, isPro = false)
    object ErinGreen: NotificationTheme(name = R.string.color_name_erin_green, color = R.color.color_name_erin_green)
    object PineGreen: NotificationTheme(name = R.string.color_name_pine_green, color = R.color.color_name_pine_green)
    object ElectricPurple: NotificationTheme(name = R.string.color_name_electric_purple, color = R.color.color_name_electric_purple)
    object SlateGray: NotificationTheme(name = R.string.color_name_slate_gray, color = R.color.color_name_slate_gray)
    object KellyGreen: NotificationTheme(name = R.string.color_name_kelly_green, color = R.color.color_name_kelly_green)
    object Artichoke: NotificationTheme(name = R.string.color_name_artichoke, color = R.color.color_name_artichoke)
    object FuchsiaPink: NotificationTheme(name = R.string.color_name_fuchsia_pink, color = R.color.color_name_fuchsia_pink)
    object MagicMint: NotificationTheme(name = R.string.color_name_magic_mint, color = R.color.color_name_magic_mint)
    object RedPigment: NotificationTheme(name = R.string.color_name_red_pigment, color = R.color.color_name_red_pigment)
    object DeepSaffron: NotificationTheme(name = R.string.color_name_deep_saffron, color = R.color.color_name_deep_saffron)
    object AquaBlue: NotificationTheme(name = R.string.color_name_aqua_blue, color = R.color.color_name_aqua_blue)
    object BlueMunsell: NotificationTheme(name = R.string.color_name_blue_munsell, color = R.color.color_name_blue_munsell)
    object ChromeYellow: NotificationTheme(name = R.string.color_name_chrome_yellow, color = R.color.color_name_chrome_yellow)
    object BlushPink: NotificationTheme(name = R.string.color_name_blush_pink, color = R.color.color_name_blush_pink)
    object BlueRYB: NotificationTheme(name = R.string.color_name_blue_ryb, color = R.color.color_name_blue_ryb)
    object EnglishViolet: NotificationTheme(name = R.string.color_name_english_violet, color = R.color.color_name_english_violet)

    companion object{
        fun getAllNotificationThemes(): List<NotificationTheme> = arrayListOf(
            White,
            RichBlack,
            ErinGreen,
            PineGreen,
            ElectricPurple,
            SlateGray,
            KellyGreen,
            Artichoke,
            FuchsiaPink,
            MagicMint,
            RedPigment,
            DeepSaffron,
            AquaBlue,
            BlueMunsell,
            ChromeYellow,
            BlushPink,
            BlueRYB,
            EnglishViolet
        )
    }
}