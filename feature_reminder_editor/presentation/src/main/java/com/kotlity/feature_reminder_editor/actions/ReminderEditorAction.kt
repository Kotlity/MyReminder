package com.kotlity.feature_reminder_editor.actions

import com.kotlity.core.Periodicity
import com.kotlity.feature_reminder_editor.models.PickerDialog

sealed interface ReminderEditorAction {

    data object OnInitiallyLoadReminderIfNeeded: ReminderEditorAction
    data class OnTitleUpdate(val title: String): ReminderEditorAction
    data class OnTimeUpdate(val time: Long): ReminderEditorAction
    data class OnDateUpdate(val date: Long): ReminderEditorAction
    data class OnPickerDialogVisibilityUpdate(val pickerDialog: PickerDialog?): ReminderEditorAction
    data class OnPeriodicityUpdate(val periodicity: Periodicity): ReminderEditorAction
    data object OnHandleTimeValidationStatus: ReminderEditorAction
    data object OnPeriodicityDismiss: ReminderEditorAction
    data object OnUpsertReminder: ReminderEditorAction
    data object OnBackClick: ReminderEditorAction
}