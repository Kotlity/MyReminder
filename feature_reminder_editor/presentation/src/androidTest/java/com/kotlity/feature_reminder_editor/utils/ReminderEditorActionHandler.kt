package com.kotlity.feature_reminder_editor.utils

import com.kotlity.core.Periodicity
import com.kotlity.feature_reminder_editor.actions.ReminderEditorAction
import com.kotlity.feature_reminder_editor.models.PickerDialog

internal inline fun reminderEditorActionHandler(
    reminderEditorAction: ReminderEditorAction,
    onTimeUpdate: (Pair<Int, Int>) -> Unit = {},
    onDateUpdate: (Long) -> Unit = {},
    onPickerDialogUpdate: (PickerDialog?) -> Unit = {},
    onCanShowTimePicker: (Boolean) -> Unit = {},
    onPeriodicityUpdate: (Periodicity) -> Unit = {},
    onPeriodicityDropdownMenuVisibilityUpdate: (Boolean) -> Unit = {},
    onHandleTimeValidationStatus: () -> Unit = {},
    onUpsertReminder: () -> Unit = {},
) {
    when(reminderEditorAction) {
        is ReminderEditorAction.OnTimeUpdate -> onTimeUpdate(reminderEditorAction.response) //
        is ReminderEditorAction.OnDateUpdate -> onDateUpdate(reminderEditorAction.date) //
        is ReminderEditorAction.OnPickerDialogUpdate -> onPickerDialogUpdate(reminderEditorAction.pickerDialog) //
        is ReminderEditorAction.OnCanShowTimePicker -> onCanShowTimePicker(reminderEditorAction.canShowTimePicker)
        is ReminderEditorAction.OnPeriodicityUpdate -> onPeriodicityUpdate(reminderEditorAction.periodicity)
        is ReminderEditorAction.OnPeriodicityDropdownMenuVisibilityUpdate -> onPeriodicityDropdownMenuVisibilityUpdate(reminderEditorAction.isExpanded)
        ReminderEditorAction.OnHandleTimeValidationStatus -> onHandleTimeValidationStatus()
        ReminderEditorAction.OnUpsertReminder -> onUpsertReminder()
        else -> {}
    }
}