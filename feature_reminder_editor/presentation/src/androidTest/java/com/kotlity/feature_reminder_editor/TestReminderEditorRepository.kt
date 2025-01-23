package com.kotlity.feature_reminder_editor

import com.kotlity.core.Reminder
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result
import com.kotlity.utils.TestErrorHandler
import com.kotlity.utils.TestReminderRepositoryWrapper

class TestReminderEditorRepository:
    TestReminderRepositoryWrapper<Reminder, DatabaseError>(),
    TestErrorHandler<ReminderError>,
    ReminderEditorRepository {

    override var error: ReminderError? = null

    override suspend fun getReminderById(id: Long): Result<Reminder?, DatabaseError> {
        if (error != null) return Result.Error(error = (error as ReminderError.Database).error)

        val reminder = reminders.find { it.id == id }

        return if (reminder != null) {
            val result = Result.Success(data = reminder)
            updateReminderState(result = result)
            result
        } else {
            val result = Result.Error(error = DatabaseError.ILLEGAL_ARGUMENT)
            updateReminderState(result = result)
            result
        }
    }

    override suspend fun upsertReminder(reminder: Reminder): Result<Unit, ReminderError> {
        return handleError {
            val isExistingReminderIndex = reminders.indexOfFirst { it.id == reminder.id }

            updateReminderState(result = Result.Success(data = reminder))
            val result = Result.Success(data = Unit)
            if (isExistingReminderIndex != -1) {
                reminders[isExistingReminderIndex] = reminder
                result
            } else {
                reminders.add(reminder)
                result
            }
        }
    }
}