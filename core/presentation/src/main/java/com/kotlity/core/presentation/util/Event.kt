package com.kotlity.core.presentation.util

import com.kotlity.core.domain.util.Error

typealias eventError = Error

sealed interface Event<out T, out E: eventError> {
    data class Success<out T>(val data: T): Event<T, Nothing>
    data class Error<out E: eventError>(val error: E): Event<Nothing, E>
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