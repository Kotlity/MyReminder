package com.kotlity.feature_reminder_editor.models

data class DisplayableReminderEditorTime(
    val value: Long,
    val hours: String,
    val minutes: String,
    val amOrPm: String?
)