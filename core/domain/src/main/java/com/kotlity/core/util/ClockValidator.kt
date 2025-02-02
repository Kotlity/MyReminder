package com.kotlity.core.util

interface ClockValidator<in R, in T, out E: ValidationError> {

    fun validate(response: R, value: T): ValidationStatus<E>
}