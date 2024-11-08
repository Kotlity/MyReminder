package com.kotlity.core.presentation.util

import android.content.Context
import com.kotlity.core.domain.util.AlarmError
import com.kotlity.core.domain.util.Error
import com.kotlity.core.domain.util.DatabaseError
import com.kotlity.core.resources.R

fun Error.toString(context: Context): String {
    val resId =  when(this) {
        DatabaseError.ILLEGAL_STATE -> R.string.databaseIllegalStateException
        DatabaseError.SQLITE_CONSTRAINT -> R.string.sqliteConstraintException
        DatabaseError.SQLITE_EXCEPTION -> R.string.sqliteException
        DatabaseError.ILLEGAL_ARGUMENT -> R.string.databaseIllegalArgumentException
        DatabaseError.UNKNOWN -> R.string.unknownException

        AlarmError.SECURITY -> R.string.alarmSecurityException
        AlarmError.ILLEGAL_ARGUMENT -> R.string.alarmIllegalArgumentException
        AlarmError.CANCELED -> R.string.alarmCanceledException
        AlarmError.UNKNOWN -> R.string.unknownException
    }
    return context.getString(resId)
}