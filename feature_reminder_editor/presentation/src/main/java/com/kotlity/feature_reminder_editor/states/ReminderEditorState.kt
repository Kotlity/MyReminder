package com.kotlity.feature_reminder_editor.states

import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.models.ReminderEditorUi
import kotlinx.serialization.Serializable

@Serializable
data class ReminderEditorState(
    val reminderEditor: ReminderEditorUi = ReminderEditorUi(),
    val pickerDialog: PickerDialog? = null,
    val isPeriodicityDropdownMenuExpanded: Boolean = false,
)