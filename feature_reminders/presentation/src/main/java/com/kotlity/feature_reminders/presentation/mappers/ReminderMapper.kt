package com.kotlity.feature_reminders.presentation.mappers

import com.kotlity.core.domain.Reminder
import com.kotlity.feature_reminders.presentation.models.ReminderUi

fun Reminder.toReminderUi(): ReminderUi {
    return ReminderUi(
        id = id,
        title = title,
        reminderTime = reminderTime.toDisplayableReminderTime(),
        periodicity = periodicity
    )
}