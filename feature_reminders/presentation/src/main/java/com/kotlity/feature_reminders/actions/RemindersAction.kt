package com.kotlity.feature_reminders.actions

sealed interface RemindersAction {
    data class OnReminderSelect(val position: Pair<Int, Int>, val id: Long): RemindersAction
    data class OnReminderEdit(val id: Long): RemindersAction
    data class OnReminderDelete(val id: Long): RemindersAction
    data object OnReminderRestore: RemindersAction
    data object OnReminderUnselect: RemindersAction
    data object OnLoadReminders: RemindersAction
}