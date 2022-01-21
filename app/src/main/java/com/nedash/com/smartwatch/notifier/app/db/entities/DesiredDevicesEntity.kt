package com.nedash.com.smartwatch.notifier.app.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "desired_devices")
data class DesiredDevicesEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "device_name")
    val deviceName: String,
//    TODO(ADD DATA ADDITIONALLY DATA ABOUT DEVICE)
)
