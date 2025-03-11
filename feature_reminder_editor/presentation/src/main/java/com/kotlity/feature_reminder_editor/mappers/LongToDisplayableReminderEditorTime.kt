package com.kotlity.feature_reminder_editor.mappers

import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTimeHourFormat
import com.kotlity.feature_reminder_editor.models.DisplayableTimeResponse
import com.kotlity.feature_reminder_editor.models.HourFormat
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

internal fun Long.toDisplayableReminderEditorTime(
    instant: Instant = Instant.ofEpochMilli(this),
    zoneId: ZoneId = ZoneId.systemDefault(),
    is24HourFormat: Boolean = true,
): DisplayableReminderEditorTime {
    val localDateTime = ZonedDateTime.ofInstant(instant, zoneId).toLocalDateTime()
    val pattern = if (is24HourFormat) "HH:mm" else "hh:mm a"
    val timeFormatter = DateTimeFormatter.ofPattern(pattern)
    val timeString = localDateTime.format(timeFormatter) // 05:25 PM or 17:25

    val (hoursAndMinutes, amOrPm) = if (!is24HourFormat) {
        val splitTime = timeString.split(" ")
        splitTime[0] to splitTime[1] // splitTime[0] - 05:25, splitTime[1] - PM
    } else timeString to null  // hoursAndMinutes - 17:25, amOrPm - null

    val (hour, minute) = hoursAndMinutes.split(":") // hours - 05 or 17, minutes - 25

    val response = Pair(first = localDateTime.hour, second = localDateTime.minute)

    val displayableResponse = DisplayableTimeResponse(
        hour = hour,
        minute = minute
    )

    val hourFormat = DisplayableReminderEditorTimeHourFormat(
        value = amOrPm, // "AM" or "PM" or null
        hourFormat = amOrPm?.let { HourFormat.valueOf(it) } // HourFormat.AM or HourFormat.PM or null
    )

    return DisplayableReminderEditorTime(
        response = response,
        displayableResponse = displayableResponse,
        hourFormat = hourFormat
    )
}