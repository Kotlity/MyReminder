package com.kotlity.core.util

sealed interface AlarmValidationError: ValidationError {

    enum class AlarmTitleValidation: AlarmValidationError {
        BLANK,
        STARTS_WITH_LOWERCASE,
        STARTS_WITH_DIGIT,
        TOO_LONG
    }

    enum class AlarmReminderTimeValidation: AlarmValidationError {
        PAST_TENSE
    }

    fun isAlarmTitleValidation() = this is AlarmTitleValidation

    fun isAlarmTimeValidation() = this is AlarmReminderTimeValidation

}