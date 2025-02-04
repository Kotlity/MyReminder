package com.kotlity.feature_reminder_editor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class DisplayableReminderEditorTime(
    val response: Pair<Int, Int> = Pair(first = 0, second = 0),
    val displayableResponse: DisplayableTimeResponse = DisplayableTimeResponse(),
    val hourFormat: DisplayableReminderEditorTimeHourFormat = DisplayableReminderEditorTimeHourFormat()
): Parcelable