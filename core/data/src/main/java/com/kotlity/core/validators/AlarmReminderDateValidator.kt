package com.kotlity.core.validators

import com.kotlity.core.Periodicity
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ClockValidator
import com.kotlity.core.util.ValidationStatus
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset

class AlarmReminderDateValidator: ClockValidator<Periodicity, Long, AlarmValidationError.AlarmReminderDateValidation> {

    override fun validate(response: Periodicity, value: Long): ValidationStatus<AlarmValidationError.AlarmReminderDateValidation> {
        val selectedDay = Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC).dayOfWeek
        val isWeekday = selectedDay in DayOfWeek.MONDAY..DayOfWeek.FRIDAY
        val isError = response == Periodicity.WEEKDAYS && !isWeekday
        return if (isError) ValidationStatus.Error(error = AlarmValidationError.AlarmReminderDateValidation.ONLY_WEEKDAYS_ALLOWED)
        else ValidationStatus.Success
    }
}