package com.kotlity.feature_reminders.utils

import com.kotlity.feature_reminders.actions.RemindersAction

internal inline fun RemindersAction.remindersActionHandler(
    onReminderSelect: (Pair<Int, Int>, Long) -> Unit = { _,_ -> },
    onReminderEdit: (Long) -> Unit = {},
    onReminderDelete: (Long) -> Unit = {},
    onReminderRestore: () -> Unit = {},
    onReminderUnselect: () -> Unit = {}
) {
    when(this) {
        is RemindersAction.OnReminderSelect -> onReminderSelect(position, id)
        is RemindersAction.OnReminderEdit -> onReminderEdit(id)
        is RemindersAction.OnReminderDelete -> onReminderDelete(id)
        RemindersAction.OnReminderRestore -> onReminderRestore()
        RemindersAction.OnReminderUnselect -> onReminderUnselect()
    }
}