package com.kotlity.feature_reminder_editor.mappers

import com.google.common.truth.Truth.assertThat
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTimeHourFormat
import com.kotlity.feature_reminder_editor.models.HourFormat
import org.junit.Test
import java.util.Calendar

class LongToDisplayableReminderEditorTimeTest {

    @Test
    fun `should display 04 as hours, 05 as minutes and HourFormat dot PM`() {
        val timestamp = Calendar.getInstance().apply {
            set(2025, 0, 8, 16, 5)
        }.timeInMillis
        val expectedResult = DisplayableReminderEditorTime(
            value = timestamp,
            hours = "04",
            minutes = "05",
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "PM", hourFormat = HourFormat.PM)
        )

        val result = timestamp.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 04 as hours, 05 as minutes and HourFormat dot AM`() {
        val timestamp = Calendar.getInstance().apply {
            set(2025, 0, 8, 4, 5)
        }.timeInMillis
        val expectedResult = DisplayableReminderEditorTime(
            value = timestamp,
            hours = "04",
            minutes = "05",
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "AM", hourFormat = HourFormat.AM)
        )

        val result = timestamp.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 04 as hours, 05 as minutes`() {
        val timestamp = Calendar.getInstance().apply {
            set(2025, 0, 8, 4, 5)
        }.timeInMillis
        val expectedResult = DisplayableReminderEditorTime(
            value = timestamp,
            hours = "04",
            minutes = "05",
            hourFormat = DisplayableReminderEditorTimeHourFormat()
        )

        val result = timestamp.toDisplayableReminderEditorTime()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 16 as hours, 05 as minutes`() {
        val timestamp = Calendar.getInstance().apply {
            set(2025, 0, 8, 16, 5)
        }.timeInMillis
        val expectedResult = DisplayableReminderEditorTime(
            value = timestamp,
            hours = "16",
            minutes = "05",
            hourFormat = DisplayableReminderEditorTimeHourFormat()
        )

        val result = timestamp.toDisplayableReminderEditorTime()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 00 as hours, 00 as minutes`() {
        val timestamp = Calendar.getInstance().apply {
            set(2025, 0, 8, 24, 0)
        }.timeInMillis
        val expectedResult = DisplayableReminderEditorTime(
            value = timestamp,
            hours = "00",
            minutes = "00",
            hourFormat = DisplayableReminderEditorTimeHourFormat()
        )

        val result = timestamp.toDisplayableReminderEditorTime()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 11 as hours, 59 as minutes and HourFormat dot PM`() {
        val timestamp = Calendar.getInstance().apply {
            set(2025, 0, 8, 23, 59)
        }.timeInMillis
        val expectedResult = DisplayableReminderEditorTime(
            value = timestamp,
            hours = "11",
            minutes = "59",
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "PM", hourFormat = HourFormat.PM)
        )

        val result = timestamp.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display 12 as hours, 00 as minutes and HourFormat dot AM`() {
        val timestamp = Calendar.getInstance().apply {
            set(2025, 0, 8, 24, 0)
        }.timeInMillis
        val expectedResult = DisplayableReminderEditorTime(
            value = timestamp,
            hours = "12",
            minutes = "00",
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "AM", hourFormat = HourFormat.AM)
        )

        val result = timestamp.toDisplayableReminderEditorTime(is24HourFormat = false)
        assertThat(result).isEqualTo(expectedResult)
    }

}