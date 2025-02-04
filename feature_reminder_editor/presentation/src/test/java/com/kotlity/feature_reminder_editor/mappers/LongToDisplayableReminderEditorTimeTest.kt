package com.kotlity.feature_reminder_editor.mappers

import com.google.common.truth.Truth.assertThat
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTimeHourFormat
import com.kotlity.feature_reminder_editor.models.DisplayableTimeResponse
import com.kotlity.feature_reminder_editor.models.HourFormat
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset

class LongToDisplayableReminderEditorTimeTest {

    private val zoneOffset = ZoneOffset.UTC

    @Test
    fun `should display 04 as hours, 05 as minutes and HourFormat dot PM`() {
        val hour = 16
        val minute = 5
        val timestamp = LocalDateTime.of(2025, 1, 15, hour, minute).atOffset(zoneOffset).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorTime(
            response = Pair(hour, minute),
            displayableResponse = DisplayableTimeResponse(hour = "04", minute = "05"),
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "PM", hourFormat = HourFormat.PM)
        )

        val result = timestamp.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 04 as hours, 05 as minutes and HourFormat dot AM`() {
        val hour = 4
        val minute = 5
        val timestamp = LocalDateTime.of(2025, 1, 15, hour, minute).atZone(zoneOffset).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorTime(
            response = Pair(hour, minute),
            displayableResponse = DisplayableTimeResponse(hour = "04", minute = "05"),
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "AM", hourFormat = HourFormat.AM)
        )

        val result = timestamp.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 04 as hours, 05 as minutes`() {
        val hour = 4
        val minute = 5
        val timestamp = LocalDateTime.of(2025, 1, 15, hour, minute).atZone(zoneOffset).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorTime(
            response = Pair(hour, minute),
            displayableResponse = DisplayableTimeResponse(hour = "04", minute = "05"),
            hourFormat = DisplayableReminderEditorTimeHourFormat()
        )

        val result = timestamp.toDisplayableReminderEditorTime()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 16 as hours, 05 as minutes`() {
        val hour = 16
        val minute = 5
        val timestamp = LocalDateTime.of(2025, 6, 20, hour, minute).atZone(zoneOffset).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorTime(
            response = Pair(hour, minute),
            displayableResponse = DisplayableTimeResponse(hour = "16", minute = "05"),
            hourFormat = DisplayableReminderEditorTimeHourFormat()
        )

        val result = timestamp.toDisplayableReminderEditorTime()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 00 as hours, 00 as minutes`() {
        val hour = 0
        val minute = 0
        val timestamp = LocalDateTime.of(2025, 6, 20, hour, minute).atZone(zoneOffset).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorTime(
            response = Pair(hour, minute),
            displayableResponse = DisplayableTimeResponse(hour = "00", minute = "00"),
            hourFormat = DisplayableReminderEditorTimeHourFormat()
        )

        val result = timestamp.toDisplayableReminderEditorTime()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 11 as hours, 59 as minutes and HourFormat dot PM`() {
        val hour = 23
        val minute = 59
        val timestamp = LocalDateTime.of(2025, 6, 20, hour, minute).atZone(zoneOffset).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorTime(
            response = Pair(hour, minute),
            displayableResponse = DisplayableTimeResponse(hour = "11", minute = "59"),
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "PM", hourFormat = HourFormat.PM)
        )

        val result = timestamp.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 12 as hours, 00 as minutes and HourFormat dot AM`() {
        val hour = 0
        val minute = 0
        val timestamp = LocalDateTime.of(2025, 6, 20, hour, minute).atZone(zoneOffset).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorTime(
            response = Pair(hour, minute),
            displayableResponse = DisplayableTimeResponse(hour = "12", minute = "00"),
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "AM", hourFormat = HourFormat.AM)
        )

        val result = timestamp.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `time picker response as 5 hour 9 minute displays as 05 hour 09 minute with HourFormat dot AM`() {
        val response = Pair(first = 5, second = 9)
        val expectedResult = DisplayableReminderEditorTime(
            response = response,
            displayableResponse = DisplayableTimeResponse(hour = "05", minute = "09"),
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "AM", hourFormat = HourFormat.AM)
        )

        val result = response.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `time picker response as 17 hour 59 minute displays as 05 hour 09 minute with HourFormat dot PM`() {
        val response = Pair(first = 17, second = 59)
        val expectedResult = DisplayableReminderEditorTime(
            response = response,
            displayableResponse = DisplayableTimeResponse(hour = "05", minute = "59"),
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "PM", hourFormat = HourFormat.PM)
        )

        val result = response.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

}