package com.nedash.com.smartwatch.notifier.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nedash.com.smartwatch.notifier.app.db.dao.AppDataDao
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity

@Database(
    entities = [AppDataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DataBaseSmartWatch: RoomDatabase() {
    abstract fun daoAppData(): AppDataDao

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