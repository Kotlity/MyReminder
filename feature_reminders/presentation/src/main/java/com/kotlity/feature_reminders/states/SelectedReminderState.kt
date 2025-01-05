package com.kotlity.feature_reminders.states

internal data class SelectedReminderState(
    val id: Long? = null,
    val position: Pair<Int, Int>? = null
)