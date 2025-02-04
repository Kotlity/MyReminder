package com.kotlity.feature_reminder_editor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class DisplayableReminderEditorTimeHourFormat(
    val value: String? = null,
    val hourFormat: HourFormat? = null,
): Parcelable