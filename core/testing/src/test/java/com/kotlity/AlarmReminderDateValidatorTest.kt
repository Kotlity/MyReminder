package com.kotlity

import com.google.common.truth.Truth.assertThat
import com.kotlity.core.Periodicity
import com.kotlity.core.di.alarmReminderDateValidatorModule
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ClockValidator
import com.kotlity.core.util.ValidationStatus
import com.kotlity.utils.DateUtil
import com.kotlity.utils.KoinDependencyProvider
import org.junit.Test
import org.koin.test.inject

class AlarmReminderDateValidatorTest: KoinDependencyProvider(modules = listOf(alarmReminderDateValidatorModule)) {

    private val alarmReminderDateValidator by inject<ClockValidator<Periodicity, Long, AlarmValidationError.AlarmReminderDateValidation>>()

    @Test
    fun `selected a weekday after selected Periodicity dot Weekdays returns ValidationStatus dot Success`() {
        val selectedWeekdayInMillis = DateUtil.findClosestWeekdayInMillis()

        val selectedPeriodicity = Periodicity.WEEKDAYS

        val expectedResult = ValidationStatus.Success

        val result = alarmReminderDateValidator.validate(response = selectedPeriodicity, value = selectedWeekdayInMillis)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `selected a weekend after selected Periodicity dot Weekdays returns ValidationStatus dot Error`() {
        val selectedWeekendInMillis = DateUtil.findClosestWeekendInMillis()

        val selectedPeriodicity = Periodicity.WEEKDAYS

        val expectedResult = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderDateValidation.ONLY_WEEKDAYS_ALLOWED)

        val result = alarmReminderDateValidator.validate(response = selectedPeriodicity, value = selectedWeekendInMillis)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `after getting ONLY_WEEKDAYS_ALLOWED error selected a weekday returns ValidationStatus dot Success`() {
        val selectedWeekendInMillis = DateUtil.findClosestWeekendInMillis()

        val selectedPeriodicity = Periodicity.WEEKDAYS

        val expectedResult = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderDateValidation.ONLY_WEEKDAYS_ALLOWED)

        val result = alarmReminderDateValidator.validate(response = selectedPeriodicity, value = selectedWeekendInMillis)
        assertThat(result).isEqualTo(expectedResult)

        val selectedWeekdayInMillis = DateUtil.findClosestWeekdayInMillis()

        val finalExpectedResult = ValidationStatus.Success

        val finalResult = alarmReminderDateValidator.validate(response = selectedPeriodicity, value = selectedWeekdayInMillis)
        assertThat(finalResult).isEqualTo(finalExpectedResult)
    }
}