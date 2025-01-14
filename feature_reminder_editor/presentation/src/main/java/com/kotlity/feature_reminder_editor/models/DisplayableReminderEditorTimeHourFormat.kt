package com.kotlity.feature_reminder_editor.models

import kotlinx.serialization.Serializable

@Serializable
data class DisplayableReminderEditorTimeHourFormat(
    val value: String? = null,
    val hourFormat: HourFormat? = null,
)
