package com.kotlity.core

import com.kotlity.TimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TestTimeFormatter: TimeFormatter {

    private val _is24HourFormat: MutableStateFlow<Boolean> = MutableStateFlow(true)

    override val is24HourFormat: Flow<Boolean>
        get() = _is24HourFormat

    override fun is24HourFormatChanged(update: Boolean) {
        _is24HourFormat.update { update }
    }
}