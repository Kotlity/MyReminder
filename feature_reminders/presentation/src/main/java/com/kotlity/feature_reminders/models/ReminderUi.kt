package com.kotlity.feature_reminders.models

import com.kotlity.core.Periodicity

data class ReminderUi(
    val id: Long,
    val title: String,
    val reminderTime: DisplayableReminderTime,
    val periodicity: Periodicity
)
