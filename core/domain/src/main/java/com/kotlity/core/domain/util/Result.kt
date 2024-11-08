package com.kotlity.core.domain.util

typealias resultError = Error

sealed interface Result<out T, out E: resultError> {
    data class Success<out T>(val data: T): Result<T, Nothing>
    data class Error<out E: resultError>(val error: E): Result<Nothing, E>
    data object Loading: Result<Nothing, Nothing>
}