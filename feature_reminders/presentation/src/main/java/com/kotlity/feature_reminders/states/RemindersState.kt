package com.kotlity.feature_reminders.states

import androidx.compose.runtime.Immutable
import com.kotlity.feature_reminders.models.ReminderUi

@Immutable
internal data class RemindersState(
    val isLoading: Boolean = false,
    val reminders: List<ReminderUi> = emptyList(),
    val selectedReminderState: SelectedReminderState = SelectedReminderState()
)