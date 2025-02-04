package com.kotlity.feature_reminder_editor

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kotlity.TimeFormatter
import com.kotlity.core.Periodicity
import com.kotlity.core.Reminder
import com.kotlity.core.ResourcesConstant._5000
import com.kotlity.core.resources.R.*
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ClockValidator
import com.kotlity.core.util.Event
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.UiText
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.Validator
import com.kotlity.core.util.onError
import com.kotlity.core.util.onSuccess
import com.kotlity.feature_reminder_editor.actions.ReminderEditorAction
import com.kotlity.feature_reminder_editor.events.ReminderEditorOneTimeEvent
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.mappers.toReminderEditorUi
import com.kotlity.feature_reminder_editor.models.DisplayableTimeResponse
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.navigation.ReminderEditorDestination
import com.kotlity.feature_reminder_editor.states.ReminderEditorState
import com.kotlity.feature_reminder_editor.utils.ReminderEditorUtils.REMINDER_EDITOR_STATE_KEY
import com.kotlity.feature_reminder_editor.utils.getCurrentState
import com.kotlity.feature_reminder_editor.utils.getTotalTimeInMillis
import com.kotlity.feature_reminder_editor.utils.updateState
import com.kotlity.permissions.Permission
import com.kotlity.permissions.PermissionsManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ReminderEditorViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val permissionsManager: PermissionsManager,
    private val reminderEditorRepository: ReminderEditorRepository,
    private val timeFormatter: TimeFormatter,
    private val titleValidator: Validator<String, AlarmValidationError.AlarmTitleValidation>,
    private val timeValidator: ClockValidator<Pair<Int, Int>, Long, AlarmValidationError.AlarmReminderTimeValidation>,
    private val dateValidator: ClockValidator<Periodicity, Long, AlarmValidationError.AlarmReminderDateValidation>
): ViewModel() {

    private val id: Long? by lazy { savedStateHandle.toRoute<ReminderEditorDestination>().id }

    private var requiredPermissions = permissionsManager.requiredPermissions

    internal val reminderEditorState: StateFlow<ReminderEditorState> = savedStateHandle.getStateFlow(
        key = REMINDER_EDITOR_STATE_KEY,
        initialValue = ReminderEditorState(requiredPermissions = requiredPermissions)
    )
        .onStart {
            onAction(ReminderEditorAction.OnInitiallyLoadReminderIfNeeded)
            observeIs24HourFormat()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(_5000.toLong()),
            initialValue = ReminderEditorState(requiredPermissions = requiredPermissions)
        )

    val permissionsToAsk = permissionsManager.permissionsToAsk
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(_5000.toLong()),
            initialValue = emptyList()
        )

    private val _eventChannel = Channel<Event<ReminderEditorOneTimeEvent, ReminderError>>()
    val eventFlow = _eventChannel.receiveAsFlow()

    var titleValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus<AlarmValidationError.AlarmTitleValidation>>(ValidationStatus.Unspecified)
    }.value
        private set

    var timeValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation>>(ValidationStatus.Unspecified)
    }.value
        private set

    var dateValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus<AlarmValidationError.AlarmReminderDateValidation>>(ValidationStatus.Unspecified)
    }.value
        private set

    fun onAction(reminderEditorAction: ReminderEditorAction) {
        when(reminderEditorAction) {
            ReminderEditorAction.OnInitiallyLoadReminderIfNeeded -> onInitiallyLoadReminderIfNeeded()
            is ReminderEditorAction.OnTitleUpdate -> onTitleUpdate(reminderEditorAction.title)
            is ReminderEditorAction.OnTimeUpdate -> onTimeUpdate(reminderEditorAction.response)
            is ReminderEditorAction.OnDateUpdate -> onDateUpdate(reminderEditorAction.date)
            is ReminderEditorAction.OnPickerDialogVisibilityUpdate -> onPickerDialogVisibilityUpdate(reminderEditorAction.pickerDialog)
            is ReminderEditorAction.OnPeriodicityUpdate -> onPeriodicityUpdate(reminderEditorAction.periodicity)
            is ReminderEditorAction.OnPermissionResult -> onPermissionResult(reminderEditorAction.permission, reminderEditorAction.isGranted)
            is ReminderEditorAction.OnPeriodicityDropdownMenuVisibilityUpdate -> onPeriodicityDropdownMenuVisibilityUpdate(reminderEditorAction.isExpanded)
            is ReminderEditorAction.OnTitleTextFieldFocusUpdate -> onTitleTextFieldFocusUpdate(isFocused = reminderEditorAction.isFocused)
            ReminderEditorAction.OnHandleTimeValidationStatus -> onHandleTimeValidationStatus()
            ReminderEditorAction.OnRemovePermission -> onRemovePermission()
            ReminderEditorAction.OnUpsertReminder -> onUpsertReminder()
            ReminderEditorAction.OnBackClick -> onBackClick()
        }
    }

    private fun onInitiallyLoadReminderIfNeeded() {
        viewModelScope.launch {
            if (id == null) return@launch

            reminderEditorRepository.getReminderById(id = id!!)
                .onSuccess { reminder ->
                    reminder?.let { notNullReminder ->
                        val is24HourFormat = timeFormatter.is24HourFormat.first()
                        savedStateHandle.updateState {
                            copy(reminderEditor = notNullReminder.toReminderEditorUi(is24HourFormat = is24HourFormat))
                        }
                    }
                }
                .onError { error ->
                    sendErrorToChannel(error = ReminderError.Database(error = error))
                }
        }
    }

    private fun onTitleUpdate(title: String) {
        val validationResult = titleValidator.validate(value = title)
        titleValidationStatus = validationResult

        savedStateHandle.updateState { copy(reminderEditor = reminderEditor.copy(title = title)) }
    }

    private fun onTimeUpdate(response: Pair<Int, Int>) {
        val currentState = savedStateHandle.getCurrentState()
        val is24HourFormat = currentState.reminderEditor.is24HourFormat
        val updatedReminderEditorTime = response.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        savedStateHandle.updateState {
            copy(reminderEditor = reminderEditor.copy(reminderEditorTime = updatedReminderEditorTime))
        }

        val displayableTimeResponse = updatedReminderEditorTime.displayableResponse
        validateTimeIfNeeded(
            response = response,
            displayableTimeResponse = displayableTimeResponse,
            dateInMillis = currentState.reminderEditor.reminderEditorDate.value
        )
    }

    private fun validateTimeIfNeeded(
        response: Pair<Int, Int>,
        displayableTimeResponse: DisplayableTimeResponse,
        dateInMillis: Long?
    ) {
        val isTimeSelected = displayableTimeResponse.hour != null && displayableTimeResponse.minute != null
        val shouldValidateTime = dateInMillis != null && dateValidationStatus.isSuccess() && isTimeSelected
        if (shouldValidateTime) timeValidationStatus = timeValidator.validate(response = response, value = dateInMillis!!)
    }

    private fun onDateUpdate(date: Long) {
        val updatedReminderEditorDate = date.toDisplayableReminderEditorDate()
        savedStateHandle.updateState {
            copy(reminderEditor = reminderEditor.copy(reminderEditorDate = updatedReminderEditorDate))
        }

        val currentState = savedStateHandle.getCurrentState()
        val periodicity = currentState.reminderEditor.periodicity
        validateDateIfNeeded(response = periodicity, value = date)
    }

    private fun validateDateIfNeeded(response: Periodicity, value: Long?) {
        if (value != null) dateValidationStatus = dateValidator.validate(response = response, value = value)
    }

    private fun onPeriodicityUpdate(periodicity: Periodicity) {
        savedStateHandle.updateState {
            copy(reminderEditor = reminderEditor.copy(periodicity = periodicity))
        }

        val currentState = savedStateHandle.getCurrentState()
        val dateInMillis = currentState.reminderEditor.reminderEditorDate.value
        validateDateIfNeeded(response = periodicity, value = dateInMillis)
    }

    private fun onPermissionResult(permission: Permission, isGranted: Boolean) {
        permissionsManager.onPermissionResult(permission = permission, isGranted = isGranted)
    }

    private fun onPeriodicityDropdownMenuVisibilityUpdate(isExpanded: Boolean) {
        savedStateHandle.updateState { copy(isPeriodicityDropdownMenuExpanded = isExpanded) }
    }

    private fun onTitleTextFieldFocusUpdate(isFocused: Boolean) {
        val currentState = savedStateHandle.getCurrentState()
        if (isFocused && currentState.isPeriodicityDropdownMenuExpanded) onPeriodicityDropdownMenuVisibilityUpdate(isExpanded = false)
    }

    private fun onPickerDialogVisibilityUpdate(pickerDialog: PickerDialog?) {
        savedStateHandle.updateState { copy(pickerDialog = pickerDialog) }
    }

    private fun onRemovePermission() {
        permissionsManager.removePermission()
    }

    private fun onUpsertReminder() {
        viewModelScope.launch {
            reminderEditorRepository.upsertReminder(getReminder())
                .onError { error ->
                    sendErrorToChannel(error = error)
                }
                .onSuccess {
                    val uiText = if (id == null) UiText.StringResource(resId = string.reminderSuccessfullyAdded)
                        else UiText.StringResource(resId = string.reminderSuccessfullyUpdated)
                    sendOneTimeEventToChannel(oneTimeEvent = ReminderEditorOneTimeEvent.OnUpsertClick(uiText = uiText))
                }
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            sendOneTimeEventToChannel(oneTimeEvent = ReminderEditorOneTimeEvent.OnBackClick)
        }
    }

    private fun observeIs24HourFormat() {
        timeFormatter.is24HourFormat
            .onEach { is24HourFormat ->
                savedStateHandle.updateState {
                    copy(reminderEditor = reminderEditor.copy(is24HourFormat = is24HourFormat))
                }
                val reminderEditor = savedStateHandle.getCurrentState().reminderEditor
                val shouldUpdateReminderEditorTime = reminderEditor.reminderEditorTime.displayableResponse.hour != null
                if (!shouldUpdateReminderEditorTime) return@onEach
                val updatedReminderEditorTime = reminderEditor.reminderEditorTime.response.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
                savedStateHandle.updateState {
                    copy(reminderEditor = reminderEditor.copy(reminderEditorTime = updatedReminderEditorTime))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getReminder(): Reminder {
        val currentState = savedStateHandle.getCurrentState()
        val time = currentState.reminderEditor.reminderEditorTime.response
        val date = currentState.reminderEditor.reminderEditorDate.value ?: 0

        val id = currentState.reminderEditor.id ?: 0L
        val title = currentState.reminderEditor.title ?: ""
        val reminderTime = getTotalTimeInMillis(time = time, date = date)
        val periodicity = currentState.reminderEditor.periodicity

        return Reminder(
            id = id,
            title = title,
            reminderTime = reminderTime,
            periodicity = periodicity
        )
    }

    private fun onHandleTimeValidationStatus() {
        val currentState = savedStateHandle.getCurrentState()
        val reminderEditorTime = currentState.reminderEditor.reminderEditorTime
        val dateInMillis = currentState.reminderEditor.reminderEditorDate.value

        validateTimeIfNeeded(
            response = reminderEditorTime.response,
            displayableTimeResponse = reminderEditorTime.displayableResponse,
            dateInMillis = dateInMillis
        )
    }

    private suspend fun sendOneTimeEventToChannel(oneTimeEvent: ReminderEditorOneTimeEvent) {
        _eventChannel.send(Event.Success(oneTimeEvent))
    }

    private suspend fun sendErrorToChannel(error: ReminderError) {
        _eventChannel.send(Event.Error(error))
    }
}