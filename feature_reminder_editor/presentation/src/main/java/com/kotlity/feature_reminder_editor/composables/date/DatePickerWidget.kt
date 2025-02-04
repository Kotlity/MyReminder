@file:OptIn(ExperimentalMaterial3Api::class)

package com.kotlity.feature_reminder_editor.composables.date

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.kotlity.core.ResourcesConstant._400
import com.kotlity.core.resources.R
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.utils.WeekdaysSelectableDates

@Composable
internal fun DatePickerWidget(
    modifier: Modifier = Modifier,
    initialSelectedDateMillis: Long?,
    selectableDates: SelectableDates,
    @StringRes dismissTextRes: Int = R.string.cancel,
    @StringRes okTextRes: Int = R.string.ok,
    shape: Shape = DatePickerDefaults.shape,
    tonalElevation: Dp = DatePickerDefaults.TonalElevation,
    colors: DatePickerColors = DatePickerDefaults.colors(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit
) {

    val isScreenHeightEnoughToShowDatePicker = LocalConfiguration.current.screenHeightDp >= _400

    val initialDisplayMode = if (isScreenHeightEnoughToShowDatePicker) DisplayMode.Picker else DisplayMode.Input

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        initialDisplayMode = initialDisplayMode,
        selectableDates = selectableDates
    )

    LaunchedEffect(key1 = isScreenHeightEnoughToShowDatePicker) {
        if (datePickerState.displayMode == DisplayMode.Input) return@LaunchedEffect
        if (!isScreenHeightEnoughToShowDatePicker && datePickerState.displayMode == DisplayMode.Picker) datePickerState.displayMode = DisplayMode.Input
    }

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val chosenDateInMillis = datePickerState.selectedDateMillis
                onConfirm(chosenDateInMillis)
                onDismiss()
            }) {
                Text(text = stringResource(id = okTextRes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = dismissTextRes))
            }
        },
        shape = shape,
        tonalElevation = tonalElevation,
        colors = colors,
        properties = properties,
        content = {
            DatePicker(
                state = datePickerState,
                showModeToggle = isScreenHeightEnoughToShowDatePicker,
                colors = colors
            )
        }
    )
}

@PreviewAnnotation
@Composable
private fun DatePickerWidgetPreview() {

    var isShowDatePickerWidget by rememberSaveable {
        mutableStateOf(false)
    }

    var displayableReminderEditorDate by rememberSaveable(saver = DisplayableReminderEditorDateSaver) {
        mutableStateOf(DisplayableReminderEditorDate())
    }

    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Button(onClick = { isShowDatePickerWidget = true }) {
                    Text(text = "Open date picker")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(text = "day: ${displayableReminderEditorDate.day}")
                    Text(text = "month: ${displayableReminderEditorDate.month}")
                    Text(text = "year: ${displayableReminderEditorDate.year}")
                }
            }

        }
        if (isShowDatePickerWidget) {
            DatePickerWidget(
                initialSelectedDateMillis = displayableReminderEditorDate.value,
                selectableDates = WeekdaysSelectableDates,
                onDismiss = { isShowDatePickerWidget = false },
                onConfirm = { chosenDateInMillis ->
                    chosenDateInMillis?.let {
                        displayableReminderEditorDate = it.toDisplayableReminderEditorDate()
                    }
                }
            )
        }
    }
}

internal object DisplayableReminderEditorDateSaver: Saver<MutableState<DisplayableReminderEditorDate>, List<String>> {

    override fun restore(value: List<String>): MutableState<DisplayableReminderEditorDate>? {
        if (value.isEmpty()) return null
        val displayableReminderEditorDate = DisplayableReminderEditorDate(
            value = value.first().toLong(),
            day = value[1],
            month = value[2],
            year = value[3]
        )
        return mutableStateOf(displayableReminderEditorDate)
    }

    override fun SaverScope.save(value: MutableState<DisplayableReminderEditorDate>): List<String>? {
        if (value.value.value == null) return null
        val response = value.value
        return listOf(response.value!!.toString(), response.day!!, response.month!!, response.year!!)
    }
}