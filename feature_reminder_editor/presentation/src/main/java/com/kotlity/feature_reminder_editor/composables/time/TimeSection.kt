package com.kotlity.feature_reminder_editor.composables.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotlity.core.ResourcesConstant._400
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.toString
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTimeHourFormat
import com.kotlity.feature_reminder_editor.models.HourFormat
import com.kotlity.feature_reminder_editor.models.PickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimeSection(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = dimen._5dp)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    displayableReminderEditorTime: DisplayableReminderEditorTime,
    is24HourFormat: Boolean,
    canShowToggleIconButton: Boolean,
    isError: Boolean,
    errorText: String?,
    pickerDialog: PickerDialog?,
    onEditorTimeWidgetClick: () -> Unit,
    onToggleTimePickerWidgetClick: () -> Unit,
    onTimePickerWidgetDismissClick: () -> Unit,
    onTimePickerWidgetConfirmClick: (Pair<Int, Int>) -> Unit
) {

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        EditorTimeTitleSection(hourFormat = displayableReminderEditorTime.hourFormat.hourFormat)
        EditorTimeWidget(
            displayableResponse = displayableReminderEditorTime.displayableResponse,
            isError = isError,
            errorText = errorText,
            onClick = onEditorTimeWidgetClick
        )
    }
    if (pickerDialog != null && pickerDialog.isTime) {
        TimePickerWidget(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large
                )
                .testTag(stringResource(id = string.timePickerWidgetTestTag)),
            timeResponse = displayableReminderEditorTime.response,
            timePickerDialog = pickerDialog.getTime,
            timeWidgetResourceProvider = if (pickerDialog.getTime.isTimePicker) TimePickerWidgetResourceProvider()
                else TimeInputWidgetResourceProvider(),
            is24HourFormat = is24HourFormat,
            canShowToggleIconButton = canShowToggleIconButton,
            onDismiss = onTimePickerWidgetDismissClick,
            onConfirm = onTimePickerWidgetConfirmClick,
            onToggleIconClick = onToggleTimePickerWidgetClick
        )
    }
}

@PreviewAnnotation
@Composable
private fun TimeSectionPreview() {

    val context = LocalContext.current
    val canShowTimePicker = LocalConfiguration.current.screenHeightDp > _400

    var pickerDialog by remember {
        mutableStateOf<PickerDialog?>(null)
    }

    val isTimePickerDialog = pickerDialog != null && pickerDialog!!.getTime == PickerDialog.Time.TIME_PICKER

    val is24HourFormat = true
    val hourFormat = DisplayableReminderEditorTimeHourFormat(value = "AM", hourFormat = HourFormat.AM)

    var displayableReminderEditorTime by remember {
        mutableStateOf(DisplayableReminderEditorTime(hourFormat = hourFormat))
    }

    val timeValidationStatus: ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation> = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TIME)

    MyReminderTheme {
        TimeSection(
            modifier = Modifier.padding(20.dp),
            displayableReminderEditorTime = displayableReminderEditorTime,
            is24HourFormat = is24HourFormat,
            canShowToggleIconButton = canShowTimePicker,
            isError = timeValidationStatus.isError(),
            errorText = if (timeValidationStatus.isError()) timeValidationStatus.getValidationError().toString(context = context) else null,
            pickerDialog = pickerDialog,
            onEditorTimeWidgetClick = {
                pickerDialog = PickerDialog.Time.TIME_PICKER
            },
            onToggleTimePickerWidgetClick = {
                pickerDialog = if (isTimePickerDialog) PickerDialog.Time.TIME_INPUT else PickerDialog.Time.TIME_PICKER
            },
            onTimePickerWidgetDismissClick = {
                pickerDialog = null
            },
            onTimePickerWidgetConfirmClick = { timeResponse ->
                pickerDialog = null
                displayableReminderEditorTime = timeResponse.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
            }
        )
    }
}