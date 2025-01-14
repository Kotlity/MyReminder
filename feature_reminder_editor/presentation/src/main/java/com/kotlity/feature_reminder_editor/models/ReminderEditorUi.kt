package com.kotlity.feature_reminder_editor.models

import com.kotlity.core.Periodicity
import kotlinx.serialization.Serializable

@Serializable
data class ReminderEditorUi(
    val id: Long? = null,
    val title: String? = null,
    val is24HourFormat: Boolean = true,
    val reminderEditorTime: DisplayableReminderEditorTime = DisplayableReminderEditorTime(),
    val reminderEditorDate: DisplayableReminderEditorDate = DisplayableReminderEditorDate(),
    val periodicity: Periodicity = Periodicity.ONCE
)