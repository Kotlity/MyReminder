package com.kotlity.core.alarm.domain

import com.kotlity.core.domain.util.AlarmValidationError
import com.kotlity.core.domain.util.Result

class AlarmReminderTimeValidator {

    operator fun invoke(selectedTimeMillis: Long): Result<Unit, AlarmValidationError.AlarmReminderTimeValidation> {
        val currentTimeMillis = System.currentTimeMillis()
        if (selectedTimeMillis < currentTimeMillis) {
            val reminderTimeSet = currentTimeMillis + 60 * 1000
            return Result.Error(error = AlarmValidationError.AlarmReminderTimeValidation.Error(reminderTimeSet = reminderTimeSet))
        }
        else return Result.Success(Unit)
    }
}