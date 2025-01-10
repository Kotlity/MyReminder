package com.kotlity.core.util

sealed interface ValidationStatus<out E: ValidationError> {
    data class Error<out E: ValidationError>(val error: E): ValidationStatus<E>
    data object Success: ValidationStatus<Nothing>
    data object Unspecified: ValidationStatus<Nothing>

    fun isError() = this is Error

    fun isSuccess() = this is Success

    fun isUnspecified() = this is Unspecified

    fun getValidationError() = (this as Error).error
}

inline fun ValidationStatus<ValidationError>.onError(action: (ValidationError) -> Unit): ValidationStatus<ValidationError> {
    if (isError()) action(this.getValidationError())
    return this
}

inline fun ValidationStatus<ValidationError>.onSuccess(action: () -> Unit): ValidationStatus<ValidationError> {
    if (isSuccess()) action()
    return this
}

inline fun ValidationStatus<ValidationError>.onUnspecified(action: () -> Unit): ValidationStatus<ValidationError> {
    if (isUnspecified()) action()
    return this
}