package com.kotlity.core.util

sealed interface AlarmValidationError: ValidationError {

    enum class AlarmTitleValidation: AlarmValidationError {
        BLANK,
        STARTS_WITH_LOWERCASE,
        STARTS_WITH_DIGIT,
        TOO_LONG
    }

    sealed interface AlarmReminderTimeValidation: AlarmValidationError {
        data object Success: AlarmReminderTimeValidation
        data class Error(val reminderTimeSet: Long): AlarmReminderTimeValidation

        fun isSuccess() = this is Success

        fun isError() = this is Error

        fun retrieveReminderTimeSet() = (this as Error).reminderTimeSet
    }

    fun isAlarmTitleValidation() = this is AlarmTitleValidation

    fun isAlarmTimeValidation() = this is AlarmReminderTimeValidation

}

inline fun AlarmValidationError.handleAlarmValidationError(
    onAlarmTitleValidationError: (AlarmValidationError.AlarmTitleValidation) -> Unit = {},
    onAlarmTimeValidation: (AlarmValidationError.AlarmReminderTimeValidation) -> Unit = {}
): AlarmValidationError {
    return when(this) {
        is AlarmValidationError.AlarmTitleValidation -> {
            onAlarmTitleValidationError(this)
            this
        }
        is AlarmValidationError.AlarmReminderTimeValidation -> {
            onAlarmTimeValidation(this)
            this
        }
    }
}