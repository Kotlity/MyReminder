package com.kotlity.feature_reminder_editor.states

import android.os.Parcelable
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.models.ReminderEditorUi
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReminderEditorState(
    val reminderEditor: ReminderEditorUi = ReminderEditorUi(),
    val pickerDialog: PickerDialog? = null,
    val isPeriodicityDropdownMenuExpanded: Boolean = false,
): Parcelable