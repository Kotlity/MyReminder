package com.kotlity.core.util

sealed interface AlarmValidationError: Error {

    enum class AlarmTitleValidation: AlarmValidationError {
        BLANK,
        STARTS_WITH_LOWERCASE,
        STARTS_WITH_DIGIT,
        TOO_LONG
    }

    sealed interface AlarmReminderTimeValidation: AlarmValidationError {
        data object Success: AlarmReminderTimeValidation
        data class Error(val reminderTimeSet: Long): AlarmReminderTimeValidation
    }
}