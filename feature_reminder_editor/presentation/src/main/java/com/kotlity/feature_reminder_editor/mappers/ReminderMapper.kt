package com.kotlity.feature_reminder_editor.mappers

import com.kotlity.core.Reminder
import com.kotlity.feature_reminder_editor.models.ReminderEditorUi

fun Reminder.toReminderEditorUi(is24HourFormat: Boolean = true): ReminderEditorUi {
    return ReminderEditorUi(
        id = id,
        title = title,
        reminderEditorTime = reminderTime.toDisplayableReminderEditorTime(),
        reminderEditorDate = reminderTime.toDisplayableReminderEditorDate(),
        periodicity = periodicity
    )
}