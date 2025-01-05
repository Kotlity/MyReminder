package com.kotlity.feature_reminders

import com.kotlity.core.Reminder
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TestRemindersRepository: RemindersRepository {

    private val reminders = mutableListOf<Reminder>()

    private val observableReminders: MutableStateFlow<Result<List<Reminder>, DatabaseError>> = MutableStateFlow(Result.Loading)

    private var reminderError: ReminderError? = null

    fun updateObservableReminders(result: Result<List<Reminder>, DatabaseError>) {
        observableReminders.update { result }
    }

    fun setReminderError(error: ReminderError) {
        reminderError = error
    }

    fun setReminders(reminders: List<Reminder>) {
        this.reminders.addAll(reminders)
        updateObservableReminders(result = Result.Success(data = reminders))
    }

    private inline fun <reified T> reminderErrorHandler(onAction: () -> Result.Success<T>): Result<T, ReminderError> {
        if (reminderError != null) return Result.Error(error = reminderError!!)
        return onAction()
    }

    override fun getAllReminders(): Flow<Result<List<Reminder>, DatabaseError>> {
        return observableReminders
    }

    override suspend fun deleteReminder(id: Long): Result<Reminder?, ReminderError> {
        return reminderErrorHandler {
            val reminderToDelete = reminders.find { it.id == id }
            reminders.removeIf { it == reminderToDelete }
            updateObservableReminders(result = Result.Success(data = reminders))
            Result.Success(data = reminderToDelete)
        }
    }

    override suspend fun restoreReminder(reminder: Reminder): Result<Unit, ReminderError> {
        return reminderErrorHandler {
            reminders.add(reminder.id.toInt(), reminder)
            updateObservableReminders(result = Result.Success(data = reminders))
            Result.Success(data = Unit)
        }
    }
}