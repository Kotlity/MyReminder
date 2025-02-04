package com.kotlity.feature_reminder_editor.mappers

import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTimeHourFormat
import com.kotlity.feature_reminder_editor.models.DisplayableTimeResponse
import com.kotlity.feature_reminder_editor.models.HourFormat
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

internal fun Pair<Int, Int>.toDisplayableReminderEditorTime(is24HourFormat: Boolean): DisplayableReminderEditorTime {
    val localTime = LocalTime.of(first, second)
    val pattern = if (is24HourFormat) "HH:mm" else "hh:mm a"
    val formattedString = localTime.format(DateTimeFormatter.ofPattern(pattern))

    val (hoursAndMinutes, amPmOrNull) = if (is24HourFormat) {
        formattedString to null
    } else {
        val splittedString = formattedString.split(" ")
        splittedString[0] to splittedString[1]
    }

    val (hour, minute) = hoursAndMinutes.split(":")

    val displayableResponse = DisplayableTimeResponse(
        hour = hour,
        minute = minute
    )

    val hourFormat = DisplayableReminderEditorTimeHourFormat(
        value = amPmOrNull,
        hourFormat = amPmOrNull?.let { HourFormat.valueOf(it) }
    )

    return DisplayableReminderEditorTime(
        response = this,
        displayableResponse = displayableResponse,
        hourFormat = hourFormat
    )
}