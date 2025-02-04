package com.kotlity.feature_reminder_editor.states

import android.os.Parcelable
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.models.ReminderEditorUi
import com.kotlity.permissions.Permission
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ReminderEditorState(
    val reminderEditor: ReminderEditorUi = ReminderEditorUi(),
    val pickerDialog: PickerDialog? = null,
    val isPeriodicityDropdownMenuExpanded: Boolean = false,
    val requiredPermissions: List<Permission> = emptyList()
): Parcelable