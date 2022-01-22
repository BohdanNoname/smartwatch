package com.nedash.com.smartwatch.notifier.app.di

import android.content.Context
import com.nedash.com.smartwatch.notifier.app.db.DataBaseSmartWatch
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun providesDataBaseSmartWatch(@ApplicationContext context: Context): DataBaseSmartWatch =
        DataBaseSmartWatch.getDataBase(context = context)

}