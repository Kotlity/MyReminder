package com.kotlity.feature_reminders.mappers

import com.kotlity.feature_reminders.models.DisplayableReminderTime
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

fun Long.toDisplayableReminderTime(
    instant: Instant = Instant.ofEpochMilli(this),
    zoneId: ZoneId = ZoneId.systemDefault(),
    is24HourFormat: Boolean = true,
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
): DisplayableReminderTime {
    val zonedDateTime = org.threeten.bp.ZonedDateTime.ofInstant(instant, zoneId)
    val timeFormatter = if (is24HourFormat) DateTimeFormatter.ofPattern("HH:mm") else DateTimeFormatter.ofPattern("hh:mm a")
    val hoursAndMinutesString = zonedDateTime.format(timeFormatter)
    val dayAndMonthAndYearString = zonedDateTime.format(dateFormatter)
    return DisplayableReminderTime(
        value = this,
        time = hoursAndMinutesString,
        date = dayAndMonthAndYearString
    )
}