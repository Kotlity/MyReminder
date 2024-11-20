package com.kotlity.core.alarm.domain

import com.kotlity.core.domain.util.AlarmValidationError
import com.kotlity.core.domain.util.Result

class AlarmReminderTimeValidator {

    operator fun invoke(selectedTimeMillis: Long, currentTimeMillis: Long): Result<Unit, AlarmValidationError.AlarmReminderTimeValidation> {
        if (selectedTimeMillis < currentTimeMillis) {
            val reminderTimeSet = currentTimeMillis + 60 * 1000
            return Result.Error(error = AlarmValidationError.AlarmReminderTimeValidation.Error(reminderTimeSet = reminderTimeSet))
        }
        return Result.Success(Unit)
    }
}