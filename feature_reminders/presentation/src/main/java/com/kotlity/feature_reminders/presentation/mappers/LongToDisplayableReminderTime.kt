package com.kotlity.feature_reminders.presentation.mappers

import com.kotlity.feature_reminders.presentation.models.DisplayableReminderTime
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

fun Long.toDisplayableReminderTime(
    instant: Instant = Instant.ofEpochMilli(this),
    zoneId: ZoneId = ZoneId.systemDefault(),
    timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm"),
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
): DisplayableReminderTime {
    val zonedDateTime = org.threeten.bp.ZonedDateTime.ofInstant(instant, zoneId)
    val hoursAndMinutesString = zonedDateTime.format(timeFormatter)
    val dayAndMonthAndYearString = zonedDateTime.format(dateFormatter)
    return DisplayableReminderTime(
        value = this,
        time = hoursAndMinutesString,
        date = dayAndMonthAndYearString
    )
}