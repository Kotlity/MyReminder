package com.kotlity.feature_reminder_editor.utils

import android.content.Context
import com.kotlity.feature_reminder_editor.events.ReminderEditorOneTimeEvent

internal inline fun ReminderEditorOneTimeEvent.handler(
    context: Context,
    onUpsert: (String) -> Unit,
    onBack: () -> Unit
) {
    when(this) {
        is ReminderEditorOneTimeEvent.OnUpsertClick -> onUpsert(uiText.asString(context = context))
        ReminderEditorOneTimeEvent.OnBackClick -> onBack()
    }
}