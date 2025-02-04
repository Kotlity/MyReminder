package com.kotlity.feature_reminder_editor.mappers

import android.content.Context
import com.kotlity.core.Periodicity
import com.kotlity.core.resources.R.*

internal fun Periodicity.mapToString(context: Context): String {
    return when(this) {
        Periodicity.ONCE -> context.getString(string.once)
        Periodicity.DAILY -> context.getString(string.daily)
        Periodicity.WEEKDAYS -> context.getString(string.monToFri)
    }
}