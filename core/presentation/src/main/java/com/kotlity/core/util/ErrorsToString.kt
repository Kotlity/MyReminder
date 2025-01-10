package com.kotlity.core.util

import android.content.Context
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.ReminderError
import com.kotlity.core.resources.R

private fun DatabaseError.toString(context: Context): String {
    val resId = when(this) {
        DatabaseError.ILLEGAL_STATE -> R.string.databaseIllegalStateException
        DatabaseError.SQLITE_CONSTRAINT -> R.string.sqliteConstraintException
        DatabaseError.SQLITE_EXCEPTION -> R.string.sqliteException
        DatabaseError.ILLEGAL_ARGUMENT -> R.string.databaseIllegalArgumentException
        DatabaseError.UNKNOWN -> R.string.unknownException
    }
    return context.getString(resId)
}

private fun AlarmError.toString(context: Context): String {
    val resId = when(this) {
        AlarmError.SECURITY -> R.string.alarmSecurityException
        AlarmError.ILLEGAL_ARGUMENT -> R.string.alarmIllegalArgumentException
        AlarmError.CANCELED -> R.string.alarmCanceledException
        AlarmError.UNKNOWN -> R.string.unknownException
    }
    return context.getString(resId)
}

fun ReminderError.toString(context: Context): String {
    return when(this) {
        is ReminderError.Alarm -> error.toString(context)
        is ReminderError.Database -> error.toString(context)
    }
}

fun AlarmValidationError.AlarmTitleValidation.toString(context: Context): String {
    val resId = when(this) {
        AlarmValidationError.AlarmTitleValidation.BLANK -> R.string.titleIsBlank
        AlarmValidationError.AlarmTitleValidation.STARTS_WITH_LOWERCASE -> R.string.titleStartsWithLowerCase
        AlarmValidationError.AlarmTitleValidation.STARTS_WITH_DIGIT -> R.string.titleStartsWithADigit
        AlarmValidationError.AlarmTitleValidation.TOO_LONG -> R.string.titleIsTooLong
    }
    return context.getString(resId)
}

fun AlarmValidationError.AlarmReminderTimeValidation.toString(context: Context): String {
    val resId = when(this) {
        is AlarmValidationError.AlarmReminderTimeValidation.Error -> R.string.reminderTimePastTense
        AlarmValidationError.AlarmReminderTimeValidation.Success -> R.string.reminderTimeSetSuccessful
    }
    return context.getString(resId)
}