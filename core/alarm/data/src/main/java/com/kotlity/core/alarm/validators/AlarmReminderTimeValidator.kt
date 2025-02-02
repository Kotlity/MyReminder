package com.kotlity.core.alarm.validators

import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.Validator

class AlarmReminderTimeValidator: Validator<Long, AlarmValidationError.AlarmReminderTimeValidation> {

    override fun validate(value: Long): ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation> {
        val currentTime = System.currentTimeMillis()
        if (value < currentTime) {
            val reminderTimeSet = currentTime + 60 * 1000
            val alarmReminderTimeValidationError = AlarmValidationError.AlarmReminderTimeValidation.Error(reminderTimeSet = reminderTimeSet)
            return ValidationStatus.Error(error = alarmReminderTimeValidationError)
        }
        return ValidationStatus.Success
    }
}