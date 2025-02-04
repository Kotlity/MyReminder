package com.kotlity.feature_reminder_editor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class DisplayableReminderEditorDate(
    val value: Long? = null,
    val day: String? = null,
    val month: String? = null,
    val year: String? = null
): Parcelable