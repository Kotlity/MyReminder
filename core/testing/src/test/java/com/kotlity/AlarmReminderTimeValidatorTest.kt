package com.kotlity

import com.google.common.truth.Truth.assertThat
import com.kotlity.core.di.alarmReminderTimeValidatorModule
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ClockValidator
import com.kotlity.core.util.ValidationStatus
import com.kotlity.utils.KoinDependencyProvider
import org.junit.Before
import org.junit.Test
import org.koin.test.inject
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset

class AlarmReminderTimeValidatorTest: KoinDependencyProvider(modules = listOf(alarmReminderTimeValidatorModule)) {

    private val alarmReminderTimeValidator by inject<ClockValidator<Pair<Int, Int>, Long, AlarmValidationError.AlarmReminderTimeValidation>>()

    private val zoneOffset: ZoneOffset = ZoneOffset.UTC
    private lateinit var localDate: LocalDate

    @Before
    fun setup() {
        localDate = LocalDate.now(zoneOffset)
    }

    @Test
    fun `alarm reminder selected future time returns validation success`() {
        val localDate = localDate.plusDays(1).atStartOfDay().toInstant(zoneOffset).toEpochMilli()

        val localTime = LocalTime.of(15, 35)
        val hour = localTime.hour
        val minute = localTime.minute

        val expectedResult = ValidationStatus.Success

        val result = alarmReminderTimeValidator.validate(response = Pair(hour, minute), value = localDate)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `alarm reminder selected past time returns validation error`() {
        val localDate = localDate.atStartOfDay().toInstant(zoneOffset).toEpochMilli()

        val localTime = LocalTime.now(zoneOffset).minusHours(1)
        val hour = localTime.hour
        val minute = localTime.minute

        val expectedResult = AlarmValidationError.AlarmReminderTimeValidation.PAST_TIME

        val result = alarmReminderTimeValidator.validate(response = Pair(hour, minute), value = localDate)
        assertThat(result).isInstanceOf(ValidationStatus.Error::class.java)
        assertThat((result as ValidationStatus.Error).error).isInstanceOf(AlarmValidationError.AlarmReminderTimeValidation::class.java)
        assertThat(result.error).isEqualTo(expectedResult)
    }

}