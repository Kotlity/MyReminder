package com.kotlity.feature_reminder_editor.models

import com.kotlity.core.Periodicity

data class ReminderEditorUi(
    val id: Long,
    val title: String,
    val reminderEditorTime: DisplayableReminderEditorTime,
    val reminderEditorDate: DisplayableReminderEditorDate,
    val periodicity: Periodicity
)