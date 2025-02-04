package com.kotlity.feature_reminder_editor.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import org.threeten.bp.LocalDate
import org.threeten.bp.Year
import org.threeten.bp.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
internal open class FutureSelectableDates: SelectableDates {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val zoneOffset = ZoneOffset.UTC
        val todayStartUtcMillis = LocalDate.now(zoneOffset).atStartOfDay().toInstant(zoneOffset).toEpochMilli()
        return utcTimeMillis >= todayStartUtcMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        val currentYear = Year.now().value
        return year >= currentYear
    }
}