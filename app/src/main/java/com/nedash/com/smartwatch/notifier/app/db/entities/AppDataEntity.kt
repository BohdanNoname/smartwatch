package com.nedash.com.smartwatch.notifier.app.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nedash.com.smartwatch.notifier.app.R

@Entity(tableName = "notification_theme")
data class AppDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "icon")
    var icon: Int = 0,
    @ColumnInfo(name = "package_name")
    var packageName: String = "",
    @ColumnInfo(name = "application_name")
    var applicationName: String = "",
    @ColumnInfo(name = "theme_name")
    var themeName: String = "White",
    @ColumnInfo(name = "notification_color")
    var notificationColor: Int = R.color.color_name_white,
    @ColumnInfo(name = "mute")
    var mute: Boolean = false
)
