package com.kotlity.feature_reminders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlity.core.domain.Reminder
import com.kotlity.core.domain.util.ReminderError
import com.kotlity.core.domain.util.onError
import com.kotlity.core.domain.util.onErrorFlow
import com.kotlity.core.domain.util.onLoadingFlow
import com.kotlity.core.domain.util.onSuccess
import com.kotlity.core.domain.util.onSuccessFlow
import com.kotlity.core.presentation.util.Event
import com.kotlity.core.presentation.util.UiText
import com.kotlity.core.resources.ResourcesConstant._5000
import com.kotlity.feature_reminders.domain.RemindersRepository
import com.kotlity.feature_reminders.presentation.actions.RemindersAction
import com.kotlity.feature_reminders.presentation.events.ReminderOneTimeEvent
import com.kotlity.feature_reminders.presentation.mappers.toReminderUi
import com.kotlity.feature_reminders.presentation.states.RemindersState
import com.kotlity.feature_reminders.presentation.states.SelectedReminderState
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
    internal val state = _state
        .onStart {
            onAction(RemindersAction.OnLoadReminders)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(_5000.toLong()),
            initialValue = RemindersState()
        )

    private val _eventChannel = Channel<Event<ReminderOneTimeEvent, ReminderError>>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private var recentlyRemovedReminder: Reminder? = null

    fun onAction(remindersAction: RemindersAction) {
        when(remindersAction) {
            is RemindersAction.OnReminderSelect -> onReminderSelect(remindersAction.position, remindersAction.id)
            is RemindersAction.OnReminderEdit -> onReminderEdit(remindersAction.id)
            is RemindersAction.OnReminderDelete -> onReminderDelete(remindersAction.id)
            RemindersAction.OnReminderRestore -> onReminderRestore()
            RemindersAction.OnReminderUnselect -> onReminderUnselect()
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

    private fun onReminderSelect(position: Pair<Int, Int>, id: Long) {
        _state.update {
            it.copy(
                selectedReminderState = it.selectedReminderState.copy(
                    id = id,
                    position = position
                )
            )
        }
    }

    private fun onReminderEdit(id: Long) {
        viewModelScope.launch {
            sendOneTimeEventToChannel(ReminderOneTimeEvent.Edit(id))
        }
        onReminderUnselect()
    }

    private fun onReminderDelete(id: Long) {
        viewModelScope.launch {
            remindersRepository.deleteReminder(id)
                .onSuccess { removedReminder ->
                    recentlyRemovedReminder = removedReminder
                    val response = UiText.StringResource(com.kotlity.core.resources.R.string.reminderSuccessfullyDeleted)
                    sendOneTimeEventToChannel(ReminderOneTimeEvent.Delete(response))
                }
                .onError { sendErrorToChannel(it) }
        }
        onReminderUnselect()
    }

    private fun onReminderRestore() {
        viewModelScope.launch {
            recentlyRemovedReminder?.let { reminder ->
                remindersRepository.restoreReminder(reminder)
                    .onError { sendErrorToChannel(it) }
            }
            recentlyRemovedReminder = null
        }
    }

    private fun onReminderUnselect() {
        _state.update {
            it.copy(selectedReminderState = SelectedReminderState())
        }
    }

    private suspend fun sendOneTimeEventToChannel(oneTimeEvent: ReminderOneTimeEvent) {
        _eventChannel.send(Event.Success(oneTimeEvent))
    }

    private suspend fun sendErrorToChannel(error: ReminderError) {
        _eventChannel.send(Event.Error(error))
    }

}