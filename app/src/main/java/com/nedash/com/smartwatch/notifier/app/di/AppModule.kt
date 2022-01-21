package com.nedash.com.smartwatch.notifier.app.di

import com.nedash.com.smartwatch.notifier.app.utils.seald_classes.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDispatcherProvider(): DispatcherProvider = object :
        DispatcherProvider {
        override fun main(): CoroutineDispatcher =
            Dispatchers.Main

        override fun io(): CoroutineDispatcher =
            Dispatchers.IO

        override fun default(): CoroutineDispatcher =
            Dispatchers.Default

        override fun unconfined(): CoroutineDispatcher =
            Dispatchers.Unconfined
    }

}