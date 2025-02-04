package com.kotlity.feature_reminder_editor.mappers

import com.kotlity.core.Reminder
import com.kotlity.feature_reminder_editor.models.ReminderEditorUi

internal fun Reminder.toReminderEditorUi(is24HourFormat: Boolean = true): ReminderEditorUi {
    return ReminderEditorUi(
        id = id,
        title = title,
        is24HourFormat = is24HourFormat,
        reminderEditorTime = reminderTime.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat),
        reminderEditorDate = reminderTime.toDisplayableReminderEditorDate(),
        periodicity = periodicity
    )
}