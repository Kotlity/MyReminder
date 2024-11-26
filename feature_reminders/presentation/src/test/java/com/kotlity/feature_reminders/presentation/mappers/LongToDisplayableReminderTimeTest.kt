package com.kotlity.feature_reminders.presentation.mappers

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class LongToDisplayableReminderTimeTest {


    @Test
    fun `should display 14th of December of 2024 with slashes`() {
        val reminderTime = ZonedDateTime.of(
            2024, 12, 14, 0, 0, 0, 0, ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        val expectedDate = "14/12/2024"
        val result = reminderTime.toDisplayableReminderTime()

        assertThat(result.date).isEqualTo(expectedDate)
    }

    @Test
    fun `should display 05 colon 25`() {
        val reminderTime = ZonedDateTime.of(
            2025, 3, 5, 5, 25, 0, 0, ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        val expectedTime = "05:25"
        val result = reminderTime.toDisplayableReminderTime()

        assertThat(result.time).isEqualTo(expectedTime)
    }

    @Test
    fun `should display 17 colon 38 and 20th of December of 2024 with slashes`() {
        val reminderTime = ZonedDateTime.of(
            2024, 12, 20, 17, 38, 0, 0, ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        val expectedTime = "17:38"
        val expectedDate = "20/12/2024"
        val result = reminderTime.toDisplayableReminderTime()

        assertThat(result.time).isEqualTo(expectedTime)
        assertThat(result.date).isEqualTo(expectedDate)
    }

    @Test
    fun `should display January of 8th of 2025 with slashes`() {
        val reminderTime = ZonedDateTime.of(
            2025, 1, 8, 0, 0, 0, 0, ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        val expectedDate = "01/08/2025"
        val result = reminderTime.toDisplayableReminderTime(
            dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        )
        assertThat(result.date).isEqualTo(expectedDate)
    }

    @Test
    fun `should display 07 colon 56 AM`() {
        val reminderTime = ZonedDateTime.of(
            2025, 4, 21, 7, 56, 0, 0, ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        val expectedTime = "07:56 AM"
        val result = reminderTime.toDisplayableReminderTime(
            timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        )
        assertThat(result.time).isEqualTo(expectedTime)
    }

    @Test
    fun `should display 12 colon 15 AM and March of 16th of 2025 with slashes`() {
        val reminderTime = ZonedDateTime.of(
            2025, 3, 16, 0, 15, 0, 0, ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        val expectedTime = "12:15 AM"
        val expectedDate = "03/16/2025"
        val result = reminderTime.toDisplayableReminderTime(
            timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"),
            dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        )

        assertThat(result.time).isEqualTo(expectedTime)
        assertThat(result.date).isEqualTo(expectedDate)
    }
}