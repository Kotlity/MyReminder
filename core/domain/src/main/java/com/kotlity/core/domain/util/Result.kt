package com.kotlity.core.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

typealias resultError = Error

sealed interface Result<out T, out E: resultError> {
    data class Success<out T>(val data: T): Result<T, Nothing>
    data class Error<out E: resultError>(val error: E): Result<Nothing, E>
    data object Loading: Result<Nothing, Nothing>
}

inline fun <T, E: resultError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return if (this is Result.Success) {
        action(data)
        this
    } else this
}

inline fun <T, E: resultError> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return if (this is Result.Error) {
        action(error)
        this
    } else this
}

inline fun <T, E: resultError> Flow<Result<T, E>>.onSuccessFlow(crossinline action: (T) -> Unit): Flow<Result<T, E>> {
    return onEach { result ->
        if (result is Result.Success) action(result.data)
    }
}

inline fun <T, E: resultError> Flow<Result<T, E>>.onErrorFlow(crossinline action: suspend (E) -> Unit): Flow<Result<T, E>> {
    return onEach { result ->
        if (result is Result.Error) action(result.error)
    }
}

inline fun <T, E: resultError> Flow<Result<T, E>>.onLoadingFlow(crossinline action: () -> Unit): Flow<Result<T, E>> {
    return onEach { result ->
        if (result is Result.Loading) action()
    }
}