package com.kotlity.feature_reminder_editor.models

import android.os.Parcelable
import com.kotlity.core.Periodicity
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ReminderEditorUi(
    val id: Long? = null,
    val title: String? = null,
    val is24HourFormat: Boolean = true,
    val reminderEditorTime: DisplayableReminderEditorTime = DisplayableReminderEditorTime(),
    val reminderEditorDate: DisplayableReminderEditorDate = DisplayableReminderEditorDate(),
    val periodicity: Periodicity = Periodicity.ONCE
): Parcelable