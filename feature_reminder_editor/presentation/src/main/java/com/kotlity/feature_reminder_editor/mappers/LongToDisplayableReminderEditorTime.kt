package com.kotlity.feature_reminder_editor.mappers

import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

fun Long.toDisplayableReminderEditorTime(
    instant: Instant = Instant.ofEpochMilli(this),
    zoneId: ZoneId = ZoneId.systemDefault(),
    is24HourFormat: Boolean = true,
): DisplayableReminderEditorTime {
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    val timeFormatter = if (is24HourFormat) DateTimeFormatter.ofPattern("HH:mm") else DateTimeFormatter.ofPattern("hh:mm a")
    val timeString = zonedDateTime.format(timeFormatter) // 05:25 PM or 17:25

    val (hoursAndMinutes, amOrPm) = if (!is24HourFormat) {
        val splitTime = timeString.split(" ")
        splitTime[0] to splitTime[1] // splitTime[0] - 05:25, splitTime[1] - PM
    } else timeString to null  // hoursAndMinutes - 17:25, amOrPm - null

    val (hours, minutes) = hoursAndMinutes.split(":") // hours - 05 or 17, minutes - 25

    return DisplayableReminderEditorTime(
        value = this,
        hours = hours,
        minutes = minutes,
        amOrPm = amOrPm
    )
}