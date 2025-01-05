package com.kotlity.feature_reminders.utils

import android.content.Context
import com.kotlity.feature_reminders.events.ReminderOneTimeEvent

inline fun ReminderOneTimeEvent.handler(
    context: Context,
    onDelete: (String) -> Unit,
    onEdit: (Long) -> Unit
) {
    when(this) {
        is ReminderOneTimeEvent.Delete -> onDelete(result.asString(context))
        is ReminderOneTimeEvent.Edit -> onEdit(id)
    }
}