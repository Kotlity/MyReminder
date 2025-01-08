package com.kotlity.feature_reminder_editor.states

import com.kotlity.feature_reminder_editor.models.ReminderEditorUi

data class ReminderEditorState(
    val reminderEditor: ReminderEditorUi? = null,
    val isTimePickerDialogVisible: Boolean = false,
    val isDatePickerDialogVisible: Boolean = false,
    val isPeriodicityDropdownMenuExpanded: Boolean = false
)
