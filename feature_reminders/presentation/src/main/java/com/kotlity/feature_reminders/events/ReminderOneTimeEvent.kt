package com.kotlity.feature_reminders.events

import com.kotlity.core.util.UiText

sealed interface ReminderOneTimeEvent {
    data class Edit(val id: Long): ReminderOneTimeEvent
    data class Delete(val result: UiText): ReminderOneTimeEvent
}