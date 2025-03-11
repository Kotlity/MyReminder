package com.kotlity.feature_reminder_editor.utils

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

internal fun getTotalTimeInMillis(
    time: Pair<Int, Int>,
    date: Long,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long {
    val localDate = Instant.ofEpochMilli(date).atZone(zoneId).toLocalDate()
    val localDateTime = localDate.atTime(time.first, time.second)
    return localDateTime.atZone(zoneId).toInstant().toEpochMilli()
}