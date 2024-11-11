package com.kotlity.core.alarm.data.util

import android.app.PendingIntent
import com.kotlity.core.domain.util.AlarmError
import com.kotlity.core.domain.util.Result

inline fun <reified T> reminderCall(
    block: () -> Result<T, AlarmError>
): Result<T, AlarmError> {
    return try {
        block()
    } catch (e: Exception) {
        val error = when(e) {
            is SecurityException -> Result.Error(AlarmError.SECURITY)
            is IllegalArgumentException -> Result.Error(AlarmError.ILLEGAL_ARGUMENT)
            is PendingIntent.CanceledException -> Result.Error(AlarmError.CANCELED)
            else -> Result.Error(AlarmError.UNKNOWN)
        }
        error
    }
}