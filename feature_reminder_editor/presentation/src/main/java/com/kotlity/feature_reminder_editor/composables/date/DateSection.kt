@file:OptIn(ExperimentalMaterial3Api::class)

package com.kotlity.feature_reminder_editor.composables.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.kotlity.core.resources.R
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.utils.WeekdaysSelectableDates

@Composable
internal fun DateSection(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = R.dimen._5dp)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    pickerDialog: PickerDialog?,
    displayableReminderEditorDate: DisplayableReminderEditorDate,
    selectableDates: SelectableDates,
    isError: Boolean,
    errorText: String?,
    onEditorDateWidgetClick: () -> Unit,
    onDateWidgetDismissClick: () -> Unit,
    onDateWidgetConfirmClick: (Long?) -> Unit
) {

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        EditorDateTitleSection()
        EditorDateWidget(
            displayableReminderEditorDate = displayableReminderEditorDate,
            isError = isError,
            errorText = errorText,
            onClick = onEditorDateWidgetClick
        )
    }
    if (pickerDialog != null && pickerDialog.isDate) {
        DatePickerWidget(
            initialSelectedDateMillis = displayableReminderEditorDate.value,
            selectableDates = selectableDates,
            onDismiss = onDateWidgetDismissClick,
            onConfirm = onDateWidgetConfirmClick
        )
    }
}

@PreviewAnnotation
@Composable
private fun DateSectionPreview() {

    var displayableReminderEditorDate by rememberSaveable(saver = DisplayableReminderEditorDateSaver) {
        mutableStateOf(DisplayableReminderEditorDate())
    }

    var pickerDialog by rememberSaveable {
        mutableStateOf<PickerDialog.Date?>(null)
    }

    MyReminderTheme {
        DateSection(
            modifier = Modifier.padding(20.dp),
            pickerDialog = pickerDialog,
            displayableReminderEditorDate = displayableReminderEditorDate,
            selectableDates = WeekdaysSelectableDates,
            isError = false,
            errorText = null,
            onEditorDateWidgetClick = {
                pickerDialog = PickerDialog.Date
            },
            onDateWidgetDismissClick = {
                pickerDialog = null
            },
            onDateWidgetConfirmClick = { chosenDateInMillis ->
                pickerDialog = null
                chosenDateInMillis?.let {
                    displayableReminderEditorDate = it.toDisplayableReminderEditorDate()
                }
            }
        )
    }
}