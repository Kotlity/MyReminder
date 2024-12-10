package com.kotlity.feature_reminders.presentation.states

import androidx.compose.runtime.Immutable
import com.kotlity.feature_reminders.presentation.models.ReminderUi

@Immutable
internal data class RemindersState(
    val isLoading: Boolean = false,
    val isAlertDialogRationaleVisible: Boolean = false,
    val reminders: List<ReminderUi> = emptyList(),
    val selectedReminderState: SelectedReminderState = SelectedReminderState()
)