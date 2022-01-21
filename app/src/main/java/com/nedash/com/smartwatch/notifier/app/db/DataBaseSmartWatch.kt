package com.nedash.com.smartwatch.notifier.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nedash.com.smartwatch.notifier.app.db.dao.AppDataDao
import com.nedash.com.smartwatch.notifier.app.db.dao.PairedDevicesDao
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity
import com.nedash.com.smartwatch.notifier.app.db.entities.PairedDeviceEntity

@Database(
    entities = [AppDataEntity::class, PairedDeviceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DataBaseSmartWatch: RoomDatabase() {
    abstract fun daoAppData(): AppDataDao
    abstract fun daoPairedDevices(): PairedDevicesDao

    companion object{
        @Volatile
        private var INSTANCE: DataBaseSmartWatch? = null

        fun getDataBase(context: Context): DataBaseSmartWatch{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBaseSmartWatch::class.java,
                    "data_base_smart_watch"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}