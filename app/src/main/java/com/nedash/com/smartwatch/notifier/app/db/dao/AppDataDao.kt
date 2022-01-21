package com.nedash.com.smartwatch.notifier.app.db.dao

import androidx.room.*
import com.nedash.com.smartwatch.notifier.app.db.entities.AppDataEntity

@Dao
interface AppDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appDataEntity: AppDataEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(appDataEntity: AppDataEntity)

    @Delete
    suspend fun delete(appDataEntity: AppDataEntity)

    @Query("SELECT * FROM notification_theme")
    suspend fun getAll(): List<AppDataEntity>

    @Query("SELECT * FROM notification_theme WHERE:applicationName = application_name")
    suspend fun getByApplicationName(applicationName: String): List<AppDataEntity>
}