package com.kotlity.feature_reminder_editor.mappers

import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

fun Long.toDisplayableReminderEditorDate(
    instant: Instant = Instant.ofEpochMilli(this),
    zoneId: ZoneId = ZoneId.systemDefault(),
    dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd:MM:yyyy")
): DisplayableReminderEditorDate {
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    val dateString = zonedDateTime.format(dateTimeFormatter)

    val (day, month, year) = dateString.split(":")

    return DisplayableReminderEditorDate(
        value = this,
        day = day,
        month = month,
        year = year
    )
}