package com.kotlity.utils

import com.kotlity.core.Reminder
import com.kotlity.core.util.Error
import com.kotlity.core.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

abstract class TestReminderRepositoryWrapper<T, E: Error>(initialState: Result<T, E> = Result.Loading) {

    protected val reminders = mutableListOf<Reminder>()

    private val reminderState: MutableStateFlow<Result<T, E>> = MutableStateFlow(initialState)

    fun retrieveReminderState(): Flow<Result<T, E>> = reminderState

    fun updateReminderState(result: Result<T, E>) {
        reminderState.update { result }
    }

    fun setReminders(reminders: List<Reminder>) {
        this.reminders.addAll(reminders)
    }
}