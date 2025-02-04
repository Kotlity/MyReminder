package com.kotlity.feature_reminder_editor.actions

import com.kotlity.core.Periodicity
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.permissions.Permission

internal sealed interface ReminderEditorAction {

    data object OnInitiallyLoadReminderIfNeeded: ReminderEditorAction
    data class OnTitleUpdate(val title: String): ReminderEditorAction
    data class OnTimeUpdate(val response: Pair<Int, Int>): ReminderEditorAction
    data class OnDateUpdate(val date: Long): ReminderEditorAction
    data class OnPickerDialogVisibilityUpdate(val pickerDialog: PickerDialog?): ReminderEditorAction
    data class OnPeriodicityUpdate(val periodicity: Periodicity): ReminderEditorAction
    data class OnPermissionResult(val permission: Permission, val isGranted: Boolean): ReminderEditorAction
    data class OnPeriodicityDropdownMenuVisibilityUpdate(val isExpanded: Boolean): ReminderEditorAction
    data class OnTitleTextFieldFocusUpdate(val isFocused: Boolean): ReminderEditorAction
    data object OnHandleTimeValidationStatus: ReminderEditorAction
    data object OnRemovePermission: ReminderEditorAction
    data object OnUpsertReminder: ReminderEditorAction
    data object OnBackClick: ReminderEditorAction
}