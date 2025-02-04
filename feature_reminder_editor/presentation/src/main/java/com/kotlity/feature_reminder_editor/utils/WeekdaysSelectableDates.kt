package com.kotlity.feature_reminder_editor.utils

import org.threeten.bp.DayOfWeek
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

internal object WeekdaysSelectableDates : FutureSelectableDates() {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        if (!super.isSelectableDate(utcTimeMillis)) return false
        val dayOfWeek = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC")).toLocalDate().dayOfWeek
        val isWeekday = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
        return isWeekday
    }
}