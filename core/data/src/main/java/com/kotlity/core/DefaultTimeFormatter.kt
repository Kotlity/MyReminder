package com.kotlity.core

import android.content.Context
import android.text.format.DateFormat
import com.kotlity.TimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultTimeFormatter(context: Context): TimeFormatter {

    private val _is24HoursFormat: MutableStateFlow<Boolean> = MutableStateFlow(DateFormat.is24HourFormat(context))

    override val is24HourFormat: Flow<Boolean> = _is24HoursFormat.asStateFlow()

    override fun is24HourFormatChanged(update: Boolean) {
        _is24HoursFormat.update { update }
    }
}