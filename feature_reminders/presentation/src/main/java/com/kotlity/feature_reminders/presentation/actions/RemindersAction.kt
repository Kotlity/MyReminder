package com.kotlity.feature_reminders.presentation.actions

sealed interface RemindersAction {
    data class OnReminderSelected(val id: Long): RemindersAction
    data class OnReminderEdit(val id: Long): RemindersAction
    data class OnReminderDelete(val id: Long): RemindersAction
    data object OnIsAlertDialogRationaleVisibleUpdate: RemindersAction
    data object OnReminderAdd: RemindersAction
    data object OnReminderUnselected: RemindersAction
    data object OnLoadReminders: RemindersAction
}