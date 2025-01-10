package com.kotlity.core.util

interface Validator<in T, out E: ValidationError> {

    fun validate(value: T): ValidationStatus<E>
}