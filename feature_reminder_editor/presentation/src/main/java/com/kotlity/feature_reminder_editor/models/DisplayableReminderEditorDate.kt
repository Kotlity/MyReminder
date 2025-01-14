package com.kotlity.feature_reminder_editor.models

import kotlinx.serialization.Serializable

@Serializable
data class DisplayableReminderEditorDate(
    val value: Long? = null,
    val day: String? = null,
    val month: String? = null,
    val year: String? = null
)
