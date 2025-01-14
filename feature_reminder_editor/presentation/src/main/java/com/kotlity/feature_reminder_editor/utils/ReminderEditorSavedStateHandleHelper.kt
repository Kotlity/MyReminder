package com.kotlity.feature_reminder_editor.utils

import androidx.lifecycle.SavedStateHandle
import com.kotlity.feature_reminder_editor.states.ReminderEditorState
import com.kotlity.feature_reminder_editor.utils.ReminderEditorUtils.REMINDER_EDITOR_STATE_KEY

fun SavedStateHandle.getCurrentState(key: String = REMINDER_EDITOR_STATE_KEY) = get<ReminderEditorState>(key = key) ?: ReminderEditorState()

inline fun SavedStateHandle.updateState(
    key: String = REMINDER_EDITOR_STATE_KEY,
    block: ReminderEditorState.() -> ReminderEditorState
) {
    val currentState = getCurrentState(key = key)
    val newState = currentState.block()
    set(key = key, value = newState)
}