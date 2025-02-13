package com.kotlity.utils

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

object DateUtil {

    private val zoneOffset = ZoneOffset.UTC
    private val localDate = LocalDate.now(zoneOffset)

    fun findClosestWeekendInMillis(
        localDate: LocalDate = this.localDate
    ): Long {
        var dateInUTC = localDate.atStartOfDay()

        while(dateInUTC.dayOfWeek != DayOfWeek.SATURDAY && dateInUTC.dayOfWeek != DayOfWeek.SUNDAY) {
            dateInUTC = dateInUTC.plusDays(1)
        }

        return dateInUTC.toInstant(zoneOffset).toEpochMilli()
    }

    fun findClosestWeekdayInMillis(
        localDate: LocalDate = this.localDate
    ): Long {
        var dateInUTC = localDate.atStartOfDay()

        while(dateInUTC.dayOfWeek !in DayOfWeek.MONDAY.. DayOfWeek.FRIDAY) {
            dateInUTC = dateInUTC.plusDays(1)
        }

        return dateInUTC.toInstant(zoneOffset).toEpochMilli()
    }
}