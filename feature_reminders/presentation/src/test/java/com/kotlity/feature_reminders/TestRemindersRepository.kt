package com.kotlity.feature_reminders

import com.kotlity.core.Reminder
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result
import com.kotlity.utils.TestErrorHandler
import com.kotlity.utils.TestReminderRepositoryWrapper
import kotlinx.coroutines.flow.Flow

class TestRemindersRepository: TestReminderRepositoryWrapper<List<Reminder>, DatabaseError>(),
    TestErrorHandler<ReminderError>,
    RemindersRepository {

    override var error: ReminderError? = null

    override fun getAllReminders(): Flow<Result<List<Reminder>, DatabaseError>> {
        return retrieveReminderState()
    }

    override suspend fun deleteReminder(id: Long): Result<Reminder?, ReminderError> {
        return handleError {
            val reminderToDelete = reminders.find { it.id == id }
            reminders.removeIf { it == reminderToDelete }
            updateReminderState(result = Result.Success(data = reminders))
            Result.Success(data = reminderToDelete)
        }
    }

    override suspend fun restoreReminder(reminder: Reminder): Result<Unit, ReminderError> {
        return handleError {
            reminders.add(reminder.id.toInt(), reminder)
            updateReminderState(result = Result.Success(data = reminders))
            Result.Success(data = Unit)
        }
    }
}