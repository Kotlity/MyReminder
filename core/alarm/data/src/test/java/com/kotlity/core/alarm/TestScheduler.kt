package com.kotlity.core.alarm

import com.kotlity.core.Reminder
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TestScheduler: Scheduler {

    private val reminders = mutableListOf<Reminder>()

    private var remindersState: MutableStateFlow<Result<List<Reminder>, AlarmError>> = MutableStateFlow(Result.Success(data = reminders))

    fun setReminders(reminders: List<Reminder>) {
        this.reminders.addAll(reminders)
        updateRemindersState(result = Result.Success(data = this.reminders))
    }

    fun updateRemindersState(result: Result<List<Reminder>, AlarmError>) {
        remindersState.update { result }
    }

    fun getReminders(): Flow<Result<List<Reminder>, AlarmError>> {
        return remindersState
    }

    override fun addOrUpdateReminder(reminder: Reminder): Result<Unit, AlarmError> {
        if (remindersState.value.isError) return Result.Error(error = remindersState.value.getError)

        val isExistingReminderIndex = reminders.indexOfFirst { it.id == reminder.id }
        if (isExistingReminderIndex != -1) reminders[isExistingReminderIndex] = reminder
        else reminders.add(reminder)

        updateRemindersState(result = Result.Success(data = reminders))
        return Result.Success(data = Unit)
    }

    override fun cancelReminder(id: Long): Result<Unit, AlarmError> {
        if (remindersState.value.isError) return Result.Error(error = remindersState.value.getError)

        val isExistingReminderIndex = reminders.indexOfFirst { it.id == id }
        return if (isExistingReminderIndex != -1) {
            reminders.removeAt(isExistingReminderIndex)
            updateRemindersState(result = Result.Success(data = reminders))
            Result.Success(data = Unit)
        } else {
            updateRemindersState(result = Result.Error(error = AlarmError.ILLEGAL_ARGUMENT))
            Result.Error(error = AlarmError.ILLEGAL_ARGUMENT)
        }
    }
}