package com.kotlity.feature_reminders.presentation.events

import com.kotlity.core.presentation.util.UiText

sealed interface ReminderOneTimeEvent {
    data class Edit(val id: Long): ReminderOneTimeEvent
    data class Delete(val result: UiText): ReminderOneTimeEvent
}