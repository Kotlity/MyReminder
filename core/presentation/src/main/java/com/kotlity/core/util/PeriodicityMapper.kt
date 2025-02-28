package com.kotlity.core.util

import android.content.Context
import com.kotlity.core.Periodicity
import com.kotlity.core.resources.R.*

fun Periodicity.mapToString(context: Context): String {
    return when(this) {
        Periodicity.ONCE -> context.getString(string.once)
        Periodicity.DAILY -> context.getString(string.daily)
        Periodicity.WEEKDAYS -> context.getString(string.monToFri)
    }
}