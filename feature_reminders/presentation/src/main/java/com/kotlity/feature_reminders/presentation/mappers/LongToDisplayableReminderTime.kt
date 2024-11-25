package com.kotlity.feature_reminders.presentation.mappers

import com.kotlity.feature_reminders.presentation.models.DisplayableReminderTime

fun Long.toDisplayableReminderTime(): DisplayableReminderTime {
    val zonedDateTime = org.threeten.bp.ZonedDateTime.ofInstant(
        org.threeten.bp.Instant.ofEpochMilli(this),
        org.threeten.bp.ZoneId.systemDefault()
    )
    val hoursAndMinutesDateTimeFormatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("HH:mm")
    val dayAndMonthAndYearDateTimeFormatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("d MMMM, yyyy")
    val hoursAndMinutesString = zonedDateTime.format(hoursAndMinutesDateTimeFormatter)
    val dayAndMonthAndYearString = zonedDateTime.format(dayAndMonthAndYearDateTimeFormatter)
    return DisplayableReminderTime(
        value = this,
        time = hoursAndMinutesString,
        calendar = dayAndMonthAndYearString
    )
}