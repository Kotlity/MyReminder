package com.kotlity.core.validators

import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ClockValidator
import com.kotlity.core.util.ValidationStatus
import org.threeten.bp.ZonedDateTime

class AlarmReminderTimeValidator: ClockValidator<Pair<Int, Int>, Long, AlarmValidationError.AlarmReminderTimeValidation> {

    override fun validate(response: Pair<Int, Int>, value: Long): ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation> {
        val hour = response.first
        val minute = response.second
        val timeInMillis = ((hour * 60 + minute) * 60 * 1000).toLong()
        val totalTime = timeInMillis + value
        val currentTime = ZonedDateTime.now().toInstant().toEpochMilli()
        if (totalTime < currentTime) return ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TIME)
        return ValidationStatus.Success
    }
}