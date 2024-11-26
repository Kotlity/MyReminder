package com.kotlity.feature_reminders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlity.core.domain.util.ReminderError
import com.kotlity.core.domain.util.onError
import com.kotlity.core.domain.util.onErrorFlow
import com.kotlity.core.domain.util.onLoadingFlow
import com.kotlity.core.domain.util.onSuccess
import com.kotlity.core.domain.util.onSuccessFlow
import com.kotlity.core.presentation.util.Event
import com.kotlity.core.presentation.util.UiText
import com.kotlity.feature_reminders.domain.RemindersRepository
import com.kotlity.feature_reminders.presentation.actions.RemindersAction
import com.kotlity.feature_reminders.presentation.events.ReminderOneTimeEvent
import com.kotlity.feature_reminders.presentation.mappers.toReminderUi
import com.kotlity.feature_reminders.presentation.states.RemindersState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RemindersViewModel(private val remindersRepository: RemindersRepository): ViewModel() {

    private val _state = MutableStateFlow(RemindersState())
    val state = _state
        .onStart {
            onAction(RemindersAction.OnLoadReminders)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RemindersState()
        )

    private val _eventChannel = Channel<Event<ReminderOneTimeEvent, ReminderError>>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onAction(remindersAction: RemindersAction) {
        when(remindersAction) {
            is RemindersAction.OnReminderSelected -> onReminderSelected(remindersAction.id)
            is RemindersAction.OnReminderEdit -> onReminderEdit(remindersAction.id)
            is RemindersAction.OnReminderDelete -> onReminderDelete(remindersAction.id)
            RemindersAction.OnReminderAdd -> onReminderAdd()
            RemindersAction.OnIsAlertDialogRationaleVisibleUpdate -> onIsAlertDialogRationaleVisibleUpdate()
            RemindersAction.OnReminderUnselected -> onReminderUnselected()
            RemindersAction.OnLoadReminders -> onLoadReminders()
        }
    }

    private fun onLoadReminders() {
        remindersRepository.getAllReminders()
            .onLoadingFlow {
                _state.update {
                    it.copy(isLoading = true)
                }
            }
            .onSuccessFlow { reminders ->
                val remindersUi = reminders.map { it.toReminderUi() }
                _state.update {
                    it.copy(
                        isLoading = false,
                        reminders = remindersUi
                    )
                }
            }
            .onErrorFlow { error ->
                _state.update {
                    it.copy(isLoading = false)
                }
                sendErrorToChannel(ReminderError.Database(error))
            }
            .launchIn(viewModelScope)
    }

    private fun onReminderSelected(id: Long) {
        _state.update {
            it.copy(selectedReminderId = id)
        }
    }

    private fun onReminderEdit(id: Long) {
        viewModelScope.launch {
            sendOneTimeEventToChannel(ReminderOneTimeEvent.Edit(id))
        }
    }

    private fun onReminderDelete(id: Long) {
        viewModelScope.launch {
            remindersRepository.deleteReminder(id)
                .onSuccess {
                    val response = UiText.StringResource(com.kotlity.core.resources.R.string.reminderSuccessfullyDeleted)
                    sendOneTimeEventToChannel(ReminderOneTimeEvent.Delete(response))
                }
                .onError { sendErrorToChannel(it) }
        }
    }

    private fun onReminderAdd() {
        viewModelScope.launch {
            sendOneTimeEventToChannel(ReminderOneTimeEvent.Add)
        }
    }

    private fun onIsAlertDialogRationaleVisibleUpdate() {
        _state.update {
            it.copy(isAlertDialogRationaleVisible = !_state.value.isAlertDialogRationaleVisible)
        }
    }

    private fun onReminderUnselected() {
        _state.update {
            it.copy(selectedReminderId = null)
        }
    }

    private suspend fun sendOneTimeEventToChannel(oneTimeEvent: ReminderOneTimeEvent) {
        _eventChannel.send(Event.Success(oneTimeEvent))
    }

    private suspend fun sendErrorToChannel(error: ReminderError) {
        _eventChannel.send(Event.Error(error))
    }

}