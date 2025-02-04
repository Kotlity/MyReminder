package com.kotlity.feature_reminder_editor.mappers

import com.google.common.truth.Truth.assertThat
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class LongToDisplayableReminderEditorDateTest {

    @Test
    fun `should display day as 08, month as 01 and year as 2025`() {
        val timestamp = LocalDate.of(2025, 1, 8).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorDate(
            value = timestamp,
            day = "08",
            month = "01",
            year = "2025"
        )

        val result = timestamp.toDisplayableReminderEditorDate()
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should display day as 15, month as 10 and year as 2024`() {
        val timestamp = LocalDate.of(2024, 10, 15).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val expectedResult = DisplayableReminderEditorDate(
            value = timestamp,
            day = "15",
            month = "10",
            year = "2024"
        )

        val result = timestamp.toDisplayableReminderEditorDate()
        assertThat(result).isEqualTo(expectedResult)
    }
}