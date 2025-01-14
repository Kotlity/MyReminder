package com.kotlity.core.alarm

import com.google.common.truth.Truth.assertThat
import com.kotlity.core.alarm.di.alarmReminderTimeValidatorModule
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.Validator
import org.junit.Test
import org.koin.test.inject

class AlarmReminderTimeValidatorTest: AlarmReminderBaseValidatorDependencyProvider(modules = listOf(alarmReminderTimeValidatorModule)) {

    private val alarmReminderTimeValidator by inject<Validator<Long, AlarmValidationError.AlarmReminderTimeValidation>>()

    @Test
    fun `alarm reminder time validation returns success`() {
        val selectedTime = System.currentTimeMillis() + 60 * 1000
        val expectedResult = ValidationStatus.Success
        val result = alarmReminderTimeValidator.validate(value = selectedTime)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `alarm reminder time validation returns error`() {
        val selectedTime = System.currentTimeMillis() - 60 * 1000
        val result = alarmReminderTimeValidator.validate(value = selectedTime)
        assertThat(result).isInstanceOf(ValidationStatus.Error::class.java)
        assertThat((result as ValidationStatus.Error).error).isInstanceOf(AlarmValidationError.AlarmReminderTimeValidation::class.java)
        assertThat(result.error).isEqualTo(AlarmValidationError.AlarmReminderTimeValidation.PAST_TENSE)
    }

}