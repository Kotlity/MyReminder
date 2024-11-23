package com.kotlity.core.presentation.util

import android.content.Context
import com.kotlity.core.domain.util.AlarmError
import com.kotlity.core.domain.util.AlarmValidationError
import com.kotlity.core.domain.util.DatabaseError
import com.kotlity.core.resources.R

fun DatabaseError.toString(context: Context): String {
    val resId = when(this) {
        DatabaseError.ILLEGAL_STATE -> R.string.databaseIllegalStateException
        DatabaseError.SQLITE_CONSTRAINT -> R.string.sqliteConstraintException
        DatabaseError.SQLITE_EXCEPTION -> R.string.sqliteException
        DatabaseError.ILLEGAL_ARGUMENT -> R.string.databaseIllegalArgumentException
        DatabaseError.UNKNOWN -> R.string.unknownException
    }
    return context.getString(resId)
}

fun AlarmError.toString(context: Context): String {
    val resId = when(this) {
        AlarmError.SECURITY -> R.string.alarmSecurityException
        AlarmError.ILLEGAL_ARGUMENT -> R.string.alarmIllegalArgumentException
        AlarmError.CANCELED -> R.string.alarmCanceledException
        AlarmError.UNKNOWN -> R.string.unknownException
    }
    return context.getString(resId)
}

fun AlarmValidationError.toString(context: Context): String {
    val resId = when(this) {
        AlarmValidationError.AlarmTitleValidation.BLANK -> R.string.titleIsBlank
        AlarmValidationError.AlarmTitleValidation.STARTS_WITH_LOWERCASE -> R.string.titleStartsWithLowerCase
        AlarmValidationError.AlarmTitleValidation.STARTS_WITH_DIGIT -> R.string.titleStartsWithADigit
        AlarmValidationError.AlarmTitleValidation.TOO_LONG -> R.string.titleIsTooLong
        is AlarmValidationError.AlarmReminderTimeValidation.Error -> R.string.reminderTimePastTense
        AlarmValidationError.AlarmReminderTimeValidation.Success -> R.string.reminderTimeSetSuccessful
    }
    return context.getString(resId)
}