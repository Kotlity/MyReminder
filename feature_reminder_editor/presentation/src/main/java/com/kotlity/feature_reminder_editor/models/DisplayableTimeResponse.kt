package com.kotlity.feature_reminder_editor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class DisplayableTimeResponse(
    val hour: String? = null,
    val minute: String? = null
): Parcelable
