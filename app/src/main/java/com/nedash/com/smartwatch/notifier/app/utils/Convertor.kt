package com.nedash.com.smartwatch.notifier.app.utils

import android.content.Context
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

object Convertor {
    fun toObject(json: JsonElement): AppDataEntity =
        Json.decodeFromJsonElement(json)

    fun toJson(data: Any): JsonElement =
        Json.encodeToJsonElement(data)

    fun Context.toAppDataEntity(packageName: String, idThemeName: Int, idNotificationColor: Int){
        val themeName = getString(idThemeName)
        val notificationColor = getColor(idNotificationColor)
        AppDataEntity(
            packageName = packageName,
            themeName = themeName,
            notificationColor = notificationColor)
    }
}