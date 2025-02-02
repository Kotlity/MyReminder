package com.kotlity.utils

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

object DateUtil {

    private val zoneOffset = ZoneOffset.UTC

    fun findClosestWeekendInMillis(): Long {
        var dateInUTC = LocalDate.now(zoneOffset).atStartOfDay()

        while(dateInUTC.dayOfWeek != DayOfWeek.SATURDAY && dateInUTC.dayOfWeek != DayOfWeek.SUNDAY) {
            dateInUTC = dateInUTC.plusDays(1)
        }

        return dateInUTC.toInstant(zoneOffset).toEpochMilli()
    }

    fun findClosestWeekdayInMillis(): Long {
        var dateInUTC = LocalDate.now(zoneOffset).atStartOfDay()

        while(dateInUTC.dayOfWeek !in DayOfWeek.MONDAY.. DayOfWeek.FRIDAY) {
            dateInUTC = dateInUTC.plusDays(1)
        }

        return dateInUTC.toInstant(zoneOffset).toEpochMilli()
    }
}