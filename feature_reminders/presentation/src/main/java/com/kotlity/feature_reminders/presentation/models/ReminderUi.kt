package com.kotlity.feature_reminders.presentation.models

import com.kotlity.core.domain.Periodicity

data class ReminderUi(
    val id: Long,
    val title: String,
    val reminderTime: DisplayableReminderTime,
    val periodicity: Periodicity
)
