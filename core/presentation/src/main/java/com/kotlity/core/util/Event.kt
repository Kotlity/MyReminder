package com.kotlity.core.util

typealias eventError = Error

sealed interface Event<out T, out E: eventError> {
    data class Success<out T>(val data: T): Event<T, Nothing>
    data class Error<out E: eventError>(val error: E): Event<Nothing, E>

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val getData: T
        get() = (this as Success).data

    val getError: E
        get() = (this as Error).error
}

inline fun <T, E: eventError> Event<T, E>.onSuccess(onEvent: (T) -> Unit): Event<T, E> {
    return if (this is Event.Success) {
        onEvent(data)
        this
    } else this
}

inline fun <T, E: eventError> Event<T, E>.onError(onEvent: (E) -> Unit): Event<T, E> {
    return if (this is Event.Error) {
        onEvent(error)
        this
    } else this
}