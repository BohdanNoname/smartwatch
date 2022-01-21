package com.nedash.com.smartwatch.notifier.app.utils.seald_classes

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    fun main(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun unconfined(): CoroutineDispatcher
}
