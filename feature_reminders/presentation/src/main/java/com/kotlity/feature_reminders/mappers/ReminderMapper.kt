package com.kotlity.feature_reminders.mappers

import com.kotlity.core.Reminder
import com.kotlity.feature_reminders.models.ReminderUi

fun Reminder.toReminderUi(): ReminderUi {
    return ReminderUi(
        id = id,
        title = title,
        reminderTime = reminderTime.toDisplayableReminderTime(),
        periodicity = periodicity
    )
}