package com.kotlity

import kotlinx.coroutines.flow.Flow

interface TimeFormatter {

    val is24HourFormat: Flow<Boolean>

    fun is24HourFormatChanged(update: Boolean)
}