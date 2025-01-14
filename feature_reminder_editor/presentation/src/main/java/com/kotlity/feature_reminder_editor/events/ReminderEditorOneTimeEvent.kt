package com.kotlity.feature_reminder_editor.events

import com.kotlity.core.util.UiText

sealed interface ReminderEditorOneTimeEvent {
    data class OnUpsertClick(val uiText: UiText): ReminderEditorOneTimeEvent
    data object OnBackClick: ReminderEditorOneTimeEvent
}