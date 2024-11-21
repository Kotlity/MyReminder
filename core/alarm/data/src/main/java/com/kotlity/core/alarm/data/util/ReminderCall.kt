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
            is SecurityException -> AlarmError.SECURITY
            is IllegalArgumentException -> AlarmError.ILLEGAL_ARGUMENT
            is PendingIntent.CanceledException -> AlarmError.CANCELED
            else -> AlarmError.UNKNOWN
        }
        Result.Error(error)
    }
}