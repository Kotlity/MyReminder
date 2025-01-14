package com.kotlity.core.validators

import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.Validator

class AlarmReminderTimeValidator: Validator<Long, AlarmValidationError.AlarmReminderTimeValidation> {

    override fun validate(value: Long): ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation> {
        val currentTime = System.currentTimeMillis()
        if (value < currentTime) return ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TENSE)
        return ValidationStatus.Success
    }
}