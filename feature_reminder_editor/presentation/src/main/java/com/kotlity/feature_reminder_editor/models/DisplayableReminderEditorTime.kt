package com.kotlity.feature_reminder_editor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DisplayableReminderEditorTime(
    val value: Long? = null,
    val hours: String? = null,
    val minutes: String? = null,
    val hourFormat: DisplayableReminderEditorTimeHourFormat = DisplayableReminderEditorTimeHourFormat()
): Parcelable