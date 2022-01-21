package com.nedash.com.smartwatch.notifier.app.utils

import android.content.Context
import androidx.annotation.IdRes
import com.nedash.com.smartwatch.notifier.app.R

class SharedPreferences(context: Context) {

    companion object {
        const val LAST_OFFER_SHOW = "LAST_OFFER_SHOW"
        const val TUTORIAL_SHOWED = "TUTORIAL_SHOWED"
        const val SHOW_NOTIFICATIONS = "SHOW_NOTIFICATIONS"
        const val MODES_FIRST_INSERT = "MODES_FIRST_INSERT"
        const val OFFER_SHOW_COUNTER = "OFFER_SHOW_COUNTER"
        const val SORT_TYPE_KEY = "SORT_TYPE_KEY"
        const val MAIN_THEME_KEY = "MAIN_THEME_KEY"
    }

    private val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    fun putValue(key: String, value: Any) {
        with(prefs.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
            }
            apply()
        }
    }

    fun getString(key: String) = prefs.getString(key, null)

    fun getInt(key: String) = prefs.getInt(key, 0)

    fun getLong(key: String) = prefs.getLong(key, 0L)

    fun getBool(key: String, default: Boolean = false) = prefs.getBoolean(key, default)
}