package com.nedash.com.smartwatch.notifier.app.utils


sealed interface EventState<T> {
    class Success<T>(var data: T?): EventState<T>
    class Error<T>(var message: String): EventState<T>
    class Loading<T>(var isLoading: Boolean = true): EventState<T>
    class Empty<T>(): EventState<T>
}

