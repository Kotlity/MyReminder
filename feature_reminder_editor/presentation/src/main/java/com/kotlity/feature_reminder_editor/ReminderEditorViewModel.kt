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
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.navigation.ReminderEditorDestination
import com.kotlity.feature_reminder_editor.states.ReminderEditorState
import com.kotlity.feature_reminder_editor.utils.ReminderEditorUtils.REMINDER_EDITOR_STATE_KEY
import com.kotlity.feature_reminder_editor.utils.getCurrentState
import com.kotlity.feature_reminder_editor.utils.updateState
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

class ReminderEditorViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val reminderEditorRepository: ReminderEditorRepository,
    private val timeFormatter: TimeFormatter,
    private val titleValidator: Validator<String, AlarmValidationError.AlarmTitleValidation>,
    private val timeValidator: Validator<Long, AlarmValidationError.AlarmReminderTimeValidation>
): ViewModel() {

    private val id = savedStateHandle.toRoute<ReminderEditorDestination>().id

    val reminderEditorState: StateFlow<ReminderEditorState> = savedStateHandle.getStateFlow(key = REMINDER_EDITOR_STATE_KEY, initialValue = ReminderEditorState())
        .onStart {
            onAction(ReminderEditorAction.OnInitiallyLoadReminderIfNeeded)
            observeIs24HourFormat()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(_5000.toLong()),
            initialValue = ReminderEditorState()
        )

    private val _eventChannel = Channel<Event<ReminderEditorOneTimeEvent, ReminderError>>()
    val eventChannel = _eventChannel.receiveAsFlow()

    var titleValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus<AlarmValidationError.AlarmTitleValidation>>(ValidationStatus.Unspecified)
    }.value
        private set

    var timeValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation>>(ValidationStatus.Unspecified)
    }.value
        private set

    var dateValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation>>(ValidationStatus.Unspecified)
    }.value
        private set

    fun onAction(reminderEditorAction: ReminderEditorAction) {
        when(reminderEditorAction) {
            ReminderEditorAction.OnInitiallyLoadReminderIfNeeded -> onInitiallyLoadReminderIfNeeded()
            is ReminderEditorAction.OnTitleUpdate -> onTitleUpdate(reminderEditorAction.title)
            is ReminderEditorAction.OnTimeUpdate -> onTimeUpdate(reminderEditorAction.time)
            is ReminderEditorAction.OnDateUpdate -> onDateUpdate(reminderEditorAction.date)
            is ReminderEditorAction.OnPickerDialogVisibilityUpdate -> onPickerDialogVisibilityUpdate(reminderEditorAction.pickerDialog)
            is ReminderEditorAction.OnPeriodicityUpdate -> onPeriodicityUpdate(reminderEditorAction.periodicity)
            ReminderEditorAction.OnHandleTimeValidationStatus -> onHandleTimeValidationStatus()
            ReminderEditorAction.OnPeriodicityDismiss -> onPeriodicityDismiss()
            ReminderEditorAction.OnUpsertReminder -> onUpsertReminder()
            ReminderEditorAction.OnBackClick -> onBackClick()
        }
    }

    private fun onInitiallyLoadReminderIfNeeded() {
        viewModelScope.launch {
            if (id == null) return@launch

            reminderEditorRepository.getReminderById(id = id)
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

    private fun onTimeUpdate(time: Long) {
        if (dateValidationStatus.isSuccess()) timeValidationStatus = validateTime(time = time)

        val currentState = savedStateHandle.getCurrentState()
        val is24HourFormat = currentState.reminderEditor.is24HourFormat
        val updatedReminderEditorTime = time.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        savedStateHandle.updateState {
            copy(reminderEditor = reminderEditor.copy(reminderEditorTime = updatedReminderEditorTime))
        }
    }

    private fun onDateUpdate(date: Long) {
        dateValidationStatus = validateTime(time = date)

        val updatedReminderEditorDate = date.toDisplayableReminderEditorDate()
        savedStateHandle.updateState {
            copy(reminderEditor = reminderEditor.copy(reminderEditorDate = updatedReminderEditorDate))
        }
    }

    private fun onPeriodicityUpdate(periodicity: Periodicity) {
        savedStateHandle.updateState {
            copy(reminderEditor = reminderEditor.copy(periodicity = periodicity))
        }
    }

    private fun onPickerDialogVisibilityUpdate(pickerDialog: PickerDialog?) {
        savedStateHandle.updateState { copy(pickerDialog = pickerDialog) }
    }

    private fun onPeriodicityDismiss() {
        savedStateHandle.updateState { copy(isPeriodicityDropdownMenuExpanded = !isPeriodicityDropdownMenuExpanded) }
    }

    private fun onUpsertReminder() {
        viewModelScope.launch {
            val upsertResult = reminderEditorRepository.upsertReminder(getReminder())
            upsertResult
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
                val reminderEditorTimestamp = reminderEditor.reminderEditorTime.value ?: return@onEach
                val updatedReminderEditorTime = reminderEditorTimestamp.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
                savedStateHandle.updateState {
                    copy(reminderEditor = reminderEditor.copy(reminderEditorTime = updatedReminderEditorTime))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getReminder(): Reminder {
        val currentState = savedStateHandle.getCurrentState()
        val id = currentState.reminderEditor.id ?: 0L
        val title = currentState.reminderEditor.title ?: ""
        val reminderTime = currentState.reminderEditor.reminderEditorDate.value ?: 0L
        val periodicity = currentState.reminderEditor.periodicity

        return Reminder(
            id = id,
            title = title,
            reminderTime = reminderTime,
            periodicity = periodicity
        )
    }

    private fun onHandleTimeValidationStatus() {
        if (dateValidationStatus.isError()) {
            timeValidationStatus = ValidationStatus.Unspecified
            return
        }

        val time = savedStateHandle.getCurrentState().reminderEditor.reminderEditorTime.value ?: 0L
        onTimeUpdate(time = time)
    }

    private fun validateTime(time: Long) = timeValidator.validate(value = time)

    private suspend fun sendOneTimeEventToChannel(oneTimeEvent: ReminderEditorOneTimeEvent) {
        _eventChannel.send(Event.Success(oneTimeEvent))
    }

    private suspend fun sendErrorToChannel(error: ReminderError) {
        _eventChannel.send(Event.Error(error))
    }
}