package com.kotlity.feature_reminder_editor.utils

internal fun getTotalTimeInMillis(time: Pair<Int, Int>, date: Long): Long {
    val hour = time.first
    val minute = time.second
    val timeInMillis = ((hour * 60 + minute) * 60 * 1000).toLong()
    return date + timeInMillis
}