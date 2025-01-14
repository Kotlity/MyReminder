package com.kotlity

import com.google.common.truth.Truth.assertThat
import com.kotlity.core.di.alarmReminderTitleValidatorModule
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.Validator
import com.kotlity.utils.KoinDependencyProvider
import org.junit.Test
import org.koin.test.inject

class AlarmReminderTitleValidatorTest: KoinDependencyProvider(modules = listOf(alarmReminderTitleValidatorModule)) {

    private val alarmReminderTitleValidator by inject<Validator<String, AlarmValidationError.AlarmTitleValidation>>()

    @Test
    fun `alarm reminder title validator with blank title returns error`() {
        val blankTitle = ""
        val expectedResult = ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.BLANK)
        val result = alarmReminderTitleValidator.validate(value = blankTitle)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `alarm reminder title validator starts with a lower case returns error`() {
        val titleThatStartsWithLowerCase = "test reminder"
        val expectedResult = ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_LOWERCASE)
        val result = alarmReminderTitleValidator.validate(value = titleThatStartsWithLowerCase)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `alarm reminder title validator starts with digit returns error`() {
        val titleThatStartsWithDigit = "1Test reminder"
        val expectedResult = ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_DIGIT)
        val result = alarmReminderTitleValidator.validate(value = titleThatStartsWithDigit)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `alarm reminder title validator too long returns error`() {
        val tooLongTitleReminder = "This is a very long test reminder so it is not accessible"
        val expectedResult = ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.TOO_LONG)
        val result = alarmReminderTitleValidator.validate(value = tooLongTitleReminder)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `alarm reminder title validator returns success`() {
        val accessibleReminderTitle = "Feed the cat"
        val expectedResult = ValidationStatus.Success
        val result = alarmReminderTitleValidator.validate(value = accessibleReminderTitle)
        assertThat(result).isEqualTo(expectedResult)
    }

}