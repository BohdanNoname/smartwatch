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

    fun Context.toAppDataEntity(applicationName: String, packageName: String,
                 idThemeName: Int, idNotificationColor: Int): AppDataEntity{
        val themeName = getString(idThemeName)
        val notificationColor = getColor(idNotificationColor)

        return AppDataEntity(
            applicationName = applicationName,
            packageName = packageName,
            themeName = themeName,
            notificationColor = notificationColor)
    }
}