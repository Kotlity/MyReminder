package com.kotlity.core.util

interface ValidationError: Error

inline fun ValidationError.handleValidationError(
    onAlarmValidationError: (AlarmValidationError) -> Unit
): ValidationError {
    if (this is AlarmValidationError) onAlarmValidationError(this)
    return this
}