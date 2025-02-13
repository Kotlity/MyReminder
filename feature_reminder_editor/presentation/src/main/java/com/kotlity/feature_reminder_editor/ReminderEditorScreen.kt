@file:OptIn(ExperimentalMaterial3Api::class)

package com.kotlity.feature_reminder_editor

import android.content.res.Configuration
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kotlity.core.Periodicity
import com.kotlity.core.ResourcesConstant._0_5
import com.kotlity.core.ResourcesConstant._0_8
import com.kotlity.core.ResourcesConstant._1f
import com.kotlity.core.ResourcesConstant._400
import com.kotlity.core.composables.NotificationsPermissionTextProvider
import com.kotlity.core.composables.PermissionDialog
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.Event
import com.kotlity.core.util.ObserveAsEvents
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.getActivity
import com.kotlity.core.util.onError
import com.kotlity.core.util.onSuccess
import com.kotlity.core.util.openAppSettings
import com.kotlity.core.util.toString
import com.kotlity.feature_reminder_editor.actions.ReminderEditorAction
import com.kotlity.feature_reminder_editor.composables.BaseWidget
import com.kotlity.feature_reminder_editor.composables.EditorHeaderSection
import com.kotlity.feature_reminder_editor.composables.TopSection
import com.kotlity.feature_reminder_editor.composables.date.DateSection
import com.kotlity.feature_reminder_editor.composables.periodicity.PeriodicitySection
import com.kotlity.feature_reminder_editor.composables.time.TimeSection
import com.kotlity.feature_reminder_editor.composables.title.TitleSection
import com.kotlity.feature_reminder_editor.events.ReminderEditorOneTimeEvent
import com.kotlity.feature_reminder_editor.mappers.mapToString
import com.kotlity.feature_reminder_editor.mappers.toPermission
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTimeHourFormat
import com.kotlity.feature_reminder_editor.models.DisplayableTimeResponse
import com.kotlity.feature_reminder_editor.models.HourFormat
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.models.ReminderEditorUi
import com.kotlity.feature_reminder_editor.models.ValidationStatuses
import com.kotlity.feature_reminder_editor.states.ReminderEditorState
import com.kotlity.feature_reminder_editor.utils.FutureSelectableDates
import com.kotlity.feature_reminder_editor.utils.WeekdaysSelectableDates
import com.kotlity.feature_reminder_editor.utils.handler
import com.kotlity.permissions.Permission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ReminderEditorScreen(
    modifier: Modifier = Modifier,
    reminderEditorViewModel: ReminderEditorViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {

    val reminderEditorState by reminderEditorViewModel.reminderEditorState.collectAsStateWithLifecycle()
    val permissionsToAsk by reminderEditorViewModel.permissionsToAsk.collectAsStateWithLifecycle()

    val validationStatuses = ValidationStatuses(
        title = reminderEditorViewModel.titleValidationStatus,
        time = reminderEditorViewModel.timeValidationStatus,
        date = reminderEditorViewModel.dateValidationStatus
    )

    val eventFlow = reminderEditorViewModel.eventFlow

    ReminderEditorScreenContent(
        modifier = modifier,
        reminderEditorState = reminderEditorState,
        validationStatuses = validationStatuses,
        permissionsToAsk = permissionsToAsk,
        eventFlow = eventFlow,
        onAction = reminderEditorViewModel::onAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar
    )
}

@Composable
internal fun ReminderEditorScreenContent(
    modifier: Modifier = Modifier,
    reminderEditorState: ReminderEditorState,
    validationStatuses: ValidationStatuses = ValidationStatuses(),
    permissionsToAsk: List<Permission> = emptyList(),
    eventFlow: Flow<Event<ReminderEditorOneTimeEvent, ReminderError>> = emptyFlow(),
    onAction: (ReminderEditorAction) -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {

    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val isScreenHeightEnough = configuration.screenHeightDp > _400

    val isPortraitOrientation = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val goToAppSettingsText = stringResource(id = string.goToAppSettings)

    var editorHeaderSectionPosition by remember {
        mutableStateOf(IntOffset.Zero)
    }

    val scrollState = rememberScrollState()

    val contentPaddingEndModifier = Modifier
        .fillMaxWidth()
        .padding(
            start = with(density) { editorHeaderSectionPosition.x.toDp() },
            top = dimensionResource(id = dimen._30dp),
            end = with(density) { editorHeaderSectionPosition.x.toDp() },
            bottom = dimensionResource(id = dimen._20dp)
        )

    val titleValidationStatus = validationStatuses.title
    val timeValidationStatus = validationStatuses.time
    val dateValidationStatus = validationStatuses.date

    val displayableReminderEditorTime = reminderEditorState.reminderEditor.reminderEditorTime
    val displayableReminderEditorDate = reminderEditorState.reminderEditor.reminderEditorDate
    val isPeriodicityDropdownMenuExpanded = reminderEditorState.isPeriodicityDropdownMenuExpanded

    val isDoneButtonEnabled = titleValidationStatus.isSuccess() && timeValidationStatus.isSuccess() && dateValidationStatus.isSuccess()

    val permissionsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
            resultMap.keys.forEach { permission ->
                val isGranted = if (resultMap.keys.contains(permission)) resultMap[permission] == true else true
                if (isGranted) onAction(ReminderEditorAction.OnUpsertReminder)
                onAction(
                    ReminderEditorAction.OnPermissionResult(
                        permission = permission.toPermission(),
                        isGranted = isGranted
                    )
                )
            }
        }

    permissionsToAsk
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Permission.NOTIFICATIONS -> NotificationsPermissionTextProvider()
                },
                isPermanentlyDeclined = context.getActivity()?.shouldShowRequestPermissionRationale(permission.mapToString()) == false,
                onDismissClick = {
                    onAction(ReminderEditorAction.OnRemovePermission)
                },
                onOkClick = {
                    onAction(ReminderEditorAction.OnRemovePermission)
                    permissionsLauncher.launch(arrayOf(permission.mapToString()))
                },
                onGoToAppSettingsClick = {
                    onAction(ReminderEditorAction.OnRemovePermission)
                    context.getActivity()?.openAppSettings(settingsPath = Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                }
            )
        }

    ObserveAsEvents(eventFlow) { event ->
        event
            .onError { reminderError ->
                val response = reminderError.toString(context)
                val isAlarmSecurityError = reminderError is ReminderError.Alarm && reminderError.error == AlarmError.SECURITY
                if (isAlarmSecurityError) {
                    val isActionPerformed = onShowSnackbar(response, goToAppSettingsText)
                    if (isActionPerformed) context.getActivity()?.openAppSettings(settingsPath = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                } else onShowSnackbar(response, null)
            }
            .onSuccess { oneTimeEvent ->
                oneTimeEvent.handler(
                    context = context,
                    onUpsert = { response ->
                        onShowSnackbar(response, null)
                        onBackClick()
                    },
                    onBack = onBackClick
                )
            }
    }

    LaunchedEffect(key1 = displayableReminderEditorDate) {
        onAction(ReminderEditorAction.OnHandleTimeValidationStatus)
    }

    LaunchedEffect(key1 = isScreenHeightEnough) {
        onAction(ReminderEditorAction.OnCanShowTimePicker(canShowTimePicker = isScreenHeightEnough))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (!isPortraitOrientation) Modifier.verticalScroll(scrollState) else Modifier)
    ) {
        TopSection(
            isDoneButtonEnabled = isDoneButtonEnabled,
            onBackClick = onBackClick,
            onDoneClick = {
                val requiredPermissions = reminderEditorState.requiredPermissions.map { it.mapToString() }.toTypedArray()
                if (requiredPermissions.isNotEmpty()) permissionsLauncher.launch(requiredPermissions)
                else onAction(ReminderEditorAction.OnUpsertReminder)
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isPortraitOrientation) _0_8 else _0_5)
                .padding(
                    top = if (isPortraitOrientation) dimensionResource(id = dimen._0dp) else dimensionResource(
                        id = dimen._15dp
                    )
                )
                .offset {
                    IntOffset(
                        x = 0,
                        y = with(density) {
                            if (isPortraitOrientation) -20.dp.roundToPx()
                            else 40.dp.roundToPx()
                        }
                    )
                }
                .align(if (isPortraitOrientation) Alignment.Center else Alignment.TopCenter),
            contentAlignment = Alignment.TopCenter
        ) {
            EditorHeaderSection(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        editorHeaderSectionPosition =
                            layoutCoordinates.calculateEditorHeaderSectionPosition(density = density)
                    }
                    .zIndex(_1f)
            )
            BaseWidget(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = editorHeaderSectionPosition.y) }
                ,
                content = {
                    Column(
                        modifier = contentPaddingEndModifier,
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = dimen._15dp))
                    ) {
                        TitleSection(
                            text = reminderEditorState.reminderEditor.title ?: "",
                            onTextChange = { newTitle ->
                                onAction(ReminderEditorAction.OnTitleUpdate(title = newTitle))
                            },
                            hint = if (titleValidationStatus.isUnspecified()) stringResource(id = string.insertTitle) else null,
                            isError = titleValidationStatus.isError(),
                            errorText = if (titleValidationStatus.isError()) titleValidationStatus.getValidationError().toString(context = context) else null,
                            onFocusChange = { isFocused ->
                                onAction(ReminderEditorAction.OnTitleTextFieldFocusUpdate(isFocused = isFocused))
                            }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TimeSection(
                                displayableReminderEditorTime = displayableReminderEditorTime,
                                is24HourFormat = reminderEditorState.reminderEditor.is24HourFormat,
                                canShowToggleIconButton = isScreenHeightEnough,
                                isError = timeValidationStatus.isError(),
                                errorText = if (timeValidationStatus.isError()) timeValidationStatus.getValidationError().toString(context = context) else null,
                                pickerDialog = reminderEditorState.pickerDialog,
                                onEditorTimeWidgetClick = {
                                    onAction(ReminderEditorAction.OnPickerDialogUpdate(pickerDialog = PickerDialog.Time.TIME_PICKER))
                                },
                                onToggleTimePickerWidgetClick = {
                                    reminderEditorState.pickerDialog?.let { dialog ->
                                        val pickerDialogOnChange = if (dialog.getTime == PickerDialog.Time.TIME_INPUT) PickerDialog.Time.TIME_PICKER else PickerDialog.Time.TIME_INPUT
                                        onAction(ReminderEditorAction.OnPeriodicityDropdownMenuVisibilityUpdate(isExpanded = false))
                                        onAction(ReminderEditorAction.OnPickerDialogUpdate(pickerDialog = pickerDialogOnChange))
                                    }
                                },
                                onTimePickerWidgetDismissClick = {
                                    onAction(ReminderEditorAction.OnPickerDialogUpdate(pickerDialog = null))
                                },
                                onTimePickerWidgetConfirmClick = { timeResponse ->
                                    onAction(ReminderEditorAction.OnPickerDialogUpdate(pickerDialog = null))
                                    onAction(ReminderEditorAction.OnTimeUpdate(response = timeResponse))
                                }
                            )
                            DateSection(
                                pickerDialog = reminderEditorState.pickerDialog,
                                canShowDatePicker = isScreenHeightEnough,
                                displayableReminderEditorDate = displayableReminderEditorDate,
                                selectableDates = if (reminderEditorState.reminderEditor.periodicity != Periodicity.WEEKDAYS) FutureSelectableDates() else WeekdaysSelectableDates,
                                isError = dateValidationStatus.isError(),
                                errorText = if (dateValidationStatus.isError()) dateValidationStatus.getValidationError().toString(context = context) else null,
                                onEditorDateWidgetClick = {
                                    onAction(ReminderEditorAction.OnPeriodicityDropdownMenuVisibilityUpdate(isExpanded = false))
                                    onAction(ReminderEditorAction.OnPickerDialogUpdate(pickerDialog = PickerDialog.Date))
                                },
                                onDateWidgetDismissClick = {
                                    onAction(ReminderEditorAction.OnPickerDialogUpdate(pickerDialog = null))
                                },
                                onDateWidgetConfirmClick = { chosenDateInMillis ->
                                    onAction(ReminderEditorAction.OnPickerDialogUpdate(pickerDialog = null))
                                    chosenDateInMillis?.let { onAction(ReminderEditorAction.OnDateUpdate(date = it)) }
                                }
                            )
                        }
                        PeriodicitySection(
                            modifier = Modifier.fillMaxWidth(),
                            periodicity = reminderEditorState.reminderEditor.periodicity,
                            isExpanded = reminderEditorState.isPeriodicityDropdownMenuExpanded,
                            onSelectedItemClick = {
                                onAction(ReminderEditorAction.OnPeriodicityDropdownMenuVisibilityUpdate(isExpanded = !isPeriodicityDropdownMenuExpanded))
                            },
                            onPeriodicityItemClick = { chosenPeriodicity ->
                                onAction(ReminderEditorAction.OnPeriodicityDropdownMenuVisibilityUpdate(isExpanded = !isPeriodicityDropdownMenuExpanded))
                                onAction(ReminderEditorAction.OnPeriodicityUpdate(periodicity = chosenPeriodicity))
                            }
                        )
                    }
                }
            )
        }
    }
}

private fun LayoutCoordinates.calculateEditorHeaderSectionPosition(density: Density): IntOffset {
    return with(density) {
        IntOffset(
            x = positionInParent().x
                .toDp()
                .roundToPx(),
            y = positionInParent().y
                .toDp()
                .roundToPx() + size.height / 2
        )
    }
}

@PreviewAnnotation
@Composable
private fun ReminderEditorScreenContentPreview() {

    val reminderEditorTime = DisplayableReminderEditorTime(
        response = Pair(first = 6, second = 30),
        displayableResponse = DisplayableTimeResponse(hour = "06", minute = "30"),
        hourFormat = DisplayableReminderEditorTimeHourFormat(value = "PM", hourFormat = HourFormat.PM)
    )

    val reminderEditorDate = DisplayableReminderEditorDate(
        value = System.currentTimeMillis(),
        day = "15",
        month = "03",
        year = "2025"
    )

    val reminderEditorUi = ReminderEditorUi(
        title = null,
        is24HourFormat = false,
        reminderEditorTime = reminderEditorTime,
        reminderEditorDate = reminderEditorDate,
        periodicity = Periodicity.WEEKDAYS
    )

    val state = ReminderEditorState(
        reminderEditor = reminderEditorUi,
        isPeriodicityDropdownMenuExpanded = false
    )

    MyReminderTheme {
        ReminderEditorScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .statusBarsPadding(),
            reminderEditorState = state,
            onAction = {},
            onBackClick = { /*TODO*/ },
            onShowSnackbar = { _, _ -> false }
        )
    }
}