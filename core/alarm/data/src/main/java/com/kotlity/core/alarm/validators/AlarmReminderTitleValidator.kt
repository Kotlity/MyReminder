package com.kotlity.core.alarm.validators

import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.Validator

private const val MAX_TITLE_LENGTH = 30

class AlarmReminderTitleValidator: Validator<String, AlarmValidationError.AlarmTitleValidation> {

    override fun validate(value: String): ValidationStatus<AlarmValidationError.AlarmTitleValidation> {
        if (value.isBlank()) return ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.BLANK)
        if (value.first().isLowerCase()) return ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_LOWERCASE)
        if (value.first().isDigit()) return ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_DIGIT)
        if (value.length > MAX_TITLE_LENGTH) return ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.TOO_LONG)
        else return ValidationStatus.Success
    }
}