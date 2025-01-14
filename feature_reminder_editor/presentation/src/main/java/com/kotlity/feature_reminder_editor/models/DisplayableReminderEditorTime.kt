package com.kotlity.feature_reminder_editor.models

import kotlinx.serialization.Serializable

@Serializable
data class DisplayableReminderEditorTime(
    val value: Long? = null,
    val hours: String? = null,
    val minutes: String? = null,
    val hourFormat: DisplayableReminderEditorTimeHourFormat = DisplayableReminderEditorTimeHourFormat()
)