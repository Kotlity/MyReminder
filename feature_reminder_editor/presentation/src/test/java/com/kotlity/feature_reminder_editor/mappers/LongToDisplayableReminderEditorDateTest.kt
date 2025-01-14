package com.kotlity.feature_reminder_editor.mappers

import com.google.common.truth.Truth.assertThat
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import org.junit.Test
import java.util.Calendar

class LongToDisplayableReminderEditorDateTest {

    @Test
    fun `should display day as 08, month as 01 and year as 2025`() {
        val timestamp = Calendar.getInstance().apply {
            set(2025, 0, 8) // month starts from 0
        }.timeInMillis
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
        val timestamp = Calendar.getInstance().apply {
            set(2024, 9, 15) // month starts from 0
        }.timeInMillis
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