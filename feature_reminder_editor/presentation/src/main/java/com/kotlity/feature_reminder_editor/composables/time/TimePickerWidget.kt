@file:OptIn(ExperimentalMaterial3Api::class)

package com.kotlity.feature_reminder_editor.composables.time

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.kotlity.core.ResourcesConstant._400
import com.kotlity.core.resources.R.*
import com.kotlity.feature_reminder_editor.models.PickerDialog

@Composable
internal fun TimePickerWidget(
    modifier: Modifier = Modifier,
    timeResponse: Pair<Int, Int>,
    timePickerDialog: PickerDialog.Time,
    timeWidgetResourceProvider: TimeWidgetResourceProvider,
    is24HourFormat: Boolean,
    colors: TimePickerColors = TimePickerDefaults.colors(),
    layoutType: TimePickerLayoutType = TimePickerDefaults.layoutType(),
    onDismiss: () -> Unit,
    onConfirm: (Pair<Int, Int>) -> Unit,
    onToggleIconClick: () -> Unit
) {

    var timePickerState = rememberTimePickerState(
        initialHour = timeResponse.first,
        initialMinute = timeResponse.second,
        is24Hour = is24HourFormat
    )

    var shouldRecreateTimePickerState by rememberSaveable {
        mutableStateOf(false)
    }

    val canShowTimePicker = LocalConfiguration.current.screenHeightDp > _400

    if (shouldRecreateTimePickerState) {
        timePickerState.is24hour = is24HourFormat
        timePickerState = rememberTimePickerState(
            initialHour = timePickerState.hour,
            initialMinute = timePickerState.minute,
            is24Hour = is24HourFormat
        )
        shouldRecreateTimePickerState = false
    }

    LaunchedEffect(key1 = is24HourFormat) {
        if (timePickerState.is24hour != is24HourFormat) shouldRecreateTimePickerState = true
    }

    TimeDialogWidget(
        modifier = modifier,
        titleRes = timeWidgetResourceProvider.titleRes,
        onDismiss = onDismiss,
        onConfirm = {
            onConfirm(Pair(first = timePickerState.hour, second = timePickerState.minute))
        },
        content = {
            if (timePickerDialog == PickerDialog.Time.TIME_PICKER && canShowTimePicker) {
                TimePicker(
                    state = timePickerState,
                    colors = colors,
                    layoutType = layoutType
                )
            } else {
                TimeInput(
                    state = timePickerState,
                    colors = colors
                )
            }
        },
        toggle = {
            if (canShowTimePicker) {
                IconButton(onClick = onToggleIconClick) {
                    Icon(
                        imageVector = timeWidgetResourceProvider.icon,
                        contentDescription = stringResource(id = timeWidgetResourceProvider.description)
                    )
                }
            }
        }
    )
}

internal interface TimeWidgetResourceProvider {

    @get:StringRes
    val titleRes: Int

    val icon: ImageVector

    @get:StringRes
    val description: Int
}

internal class TimePickerWidgetResourceProvider: TimeWidgetResourceProvider {

    override val titleRes: Int = string.selectTimeTitle
    override val icon: ImageVector = Icons.Outlined.Keyboard
    override val description: Int = string.selectTimeTitleDescription
}

internal class TimeInputWidgetResourceProvider: TimeWidgetResourceProvider {

    override val titleRes: Int = string.enterTimeTitle
    override val icon: ImageVector = Icons.Outlined.AccessTime
    override val description: Int = string.inputTimeTitleDescription
}