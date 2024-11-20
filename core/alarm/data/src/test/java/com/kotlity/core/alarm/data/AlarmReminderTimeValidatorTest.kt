package com.kotlity.core.alarm.data

import com.google.common.truth.Truth.assertThat
import com.kotlity.core.alarm.domain.AlarmReminderTimeValidator
import com.kotlity.core.domain.util.AlarmValidationError
import com.kotlity.core.domain.util.Result
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Test
import kotlin.test.fail

class AlarmReminderTimeValidatorTest: BaseValidator() {

    @MockK
    private lateinit var alarmReminderTimeValidator: AlarmReminderTimeValidator

    @Test
    fun `alarm reminder time validation returns success`() {
        val currentTimeMillis = System.currentTimeMillis()
        every { alarmReminderTimeValidator(selectedTimeMillis = currentTimeMillis.plus(10000), currentTimeMillis = currentTimeMillis) } returns Result.Success(Unit)
        alarmReminderTimeValidator(selectedTimeMillis = currentTimeMillis.plus(10000), currentTimeMillis = currentTimeMillis)
        verify { alarmReminderTimeValidator(selectedTimeMillis = currentTimeMillis.plus(10000), currentTimeMillis = currentTimeMillis) }
        assertThat(alarmReminderTimeValidator(selectedTimeMillis = currentTimeMillis.plus(10000), currentTimeMillis = currentTimeMillis)).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `alarm reminder time validation returns error with reminderTimeSet`() {
        val currentTimeMillis = System.currentTimeMillis()
        val expectedReminderTimeSet = currentTimeMillis + 60 * 1000
        every { alarmReminderTimeValidator(selectedTimeMillis = currentTimeMillis.minus(10000), currentTimeMillis = currentTimeMillis) } returns Result.Error(error = AlarmValidationError.AlarmReminderTimeValidation.Error(reminderTimeSet = expectedReminderTimeSet))
        val futureTimeReminderMillis = alarmReminderTimeValidator(selectedTimeMillis = currentTimeMillis.minus(10000), currentTimeMillis = currentTimeMillis)
        alarmReminderTimeValidator(selectedTimeMillis = currentTimeMillis.minus(10000), currentTimeMillis = currentTimeMillis)
        verify { alarmReminderTimeValidator(selectedTimeMillis = currentTimeMillis.minus(10000), currentTimeMillis = currentTimeMillis) }
        assertThat(futureTimeReminderMillis).isInstanceOf(Result.Error::class.java)
        if (futureTimeReminderMillis is Result.Error && futureTimeReminderMillis.error is AlarmValidationError.AlarmReminderTimeValidation.Error) {
            val reminderTimeSet = (futureTimeReminderMillis.error as AlarmValidationError.AlarmReminderTimeValidation.Error).reminderTimeSet
            assertThat(reminderTimeSet).isEqualTo(expectedReminderTimeSet)
        } else fail("Expected Result.Error with AlarmValidationError.AlarmReminderTimeValidation.Error")
    }

}