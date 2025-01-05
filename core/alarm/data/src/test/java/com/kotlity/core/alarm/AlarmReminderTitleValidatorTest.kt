package com.kotlity.core.alarm

import com.google.common.truth.Truth.assertThat
import com.kotlity.core.alarm.validators.AlarmReminderTitleValidator
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.Result
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Test

class AlarmReminderTitleValidatorTest: BaseValidator() {

    @MockK
    private lateinit var alarmReminderTitleValidator: AlarmReminderTitleValidator

    @Test
    fun `alarm reminder title validator with blank title returns error`() {
        val blankTitle = ""
        val result = Result.Error(error = AlarmValidationError.AlarmTitleValidation.BLANK)
        every { alarmReminderTitleValidator(blankTitle) } returns result
        alarmReminderTitleValidator(blankTitle)
        verify { alarmReminderTitleValidator(blankTitle) }
        assertThat(alarmReminderTitleValidator(blankTitle)).isEqualTo(result)
    }

    @Test
    fun `alarm reminder title validator starts with a lower case returns error`() {
        val titleThatStartsWithLowerCase = "test reminder"
        val result = Result.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_LOWERCASE)
        every { alarmReminderTitleValidator(titleThatStartsWithLowerCase) } returns result
        alarmReminderTitleValidator(titleThatStartsWithLowerCase)
        verify { alarmReminderTitleValidator(titleThatStartsWithLowerCase) }
        assertThat(alarmReminderTitleValidator(titleThatStartsWithLowerCase)).isEqualTo(result)
    }

    @Test
    fun `alarm reminder title validator starts with digit returns error`() {
        val titleThatStartsWithDigit = "1Test reminder"
        val result = Result.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_DIGIT)
        every { alarmReminderTitleValidator(titleThatStartsWithDigit) } returns result
        alarmReminderTitleValidator(titleThatStartsWithDigit)
        verify { alarmReminderTitleValidator(titleThatStartsWithDigit) }
        assertThat(alarmReminderTitleValidator(titleThatStartsWithDigit)).isEqualTo(result)
    }

    @Test
    fun `alarm reminder title validator too long returns error`() {
        val tooLongTitleReminder = "This is a very long test reminder so it is not accessible"
        val result = Result.Error(error = AlarmValidationError.AlarmTitleValidation.TOO_LONG)
        every { alarmReminderTitleValidator(tooLongTitleReminder) } returns result
        alarmReminderTitleValidator(tooLongTitleReminder)
        verify { alarmReminderTitleValidator(tooLongTitleReminder) }
        assertThat(alarmReminderTitleValidator(tooLongTitleReminder)).isEqualTo(result)
    }

    @Test
    fun `alarm reminder title validator returns success`() {
        val accessibleReminderTitle = "Feed the cat"
        val result = Result.Success(Unit)
        every { alarmReminderTitleValidator(accessibleReminderTitle) } returns result
        alarmReminderTitleValidator(accessibleReminderTitle)
        verify { alarmReminderTitleValidator(accessibleReminderTitle) }
        assertThat(alarmReminderTitleValidator(accessibleReminderTitle)).isEqualTo(result)
    }

}