package com.kotlity.feature_reminders.utils

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class DateUtil {

    private val localDate = LocalDate.now()

    private val zoneId = ZoneId.systemDefault()

    private val zonedDateTime = ZonedDateTime.now(zoneId)

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val timeInMillis = Instant.now().toEpochMilli()

    val expectedTime = zonedDateTime.format(timeFormatter)

    val expectedDate = zonedDateTime.format(dateFormatter)
}