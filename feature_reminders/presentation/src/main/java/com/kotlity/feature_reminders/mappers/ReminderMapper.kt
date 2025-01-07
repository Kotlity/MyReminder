package com.kotlity.feature_reminders.mappers

import com.kotlity.core.Reminder
import com.kotlity.feature_reminders.models.ReminderUi

fun Reminder.toReminderUi(is24HourFormat: Boolean = true): ReminderUi {
    return ReminderUi(
        id = id,
        title = title,
        reminderTime = reminderTime.toDisplayableReminderTime(is24HourFormat = is24HourFormat),
        periodicity = periodicity
    )
}