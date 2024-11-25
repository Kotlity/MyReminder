package com.kotlity.feature_reminders.presentation.states

import androidx.compose.runtime.Immutable
import com.kotlity.feature_reminders.presentation.models.ReminderUi

@Immutable
data class RemindersState(
    val isLoading: Boolean = false,
    val isAlertDialogRationaleVisible: Boolean = false,
    val reminders: List<ReminderUi> = emptyList(),
    val selectedReminderId: Long? = null
)