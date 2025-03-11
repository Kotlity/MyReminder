package com.kotlity.core.alarm

import com.kotlity.core.Reminder
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.Result
import com.kotlity.utils.TestErrorHandler
import com.kotlity.utils.TestReminderRepositoryWrapper

class TestScheduler(
    initialState: Result<List<Reminder>, AlarmError> = Result.Success(data = emptyList()),
    override val canScheduleAlarms: Boolean = true
): TestReminderRepositoryWrapper<List<Reminder>, AlarmError>(initialState = initialState),
    TestErrorHandler<AlarmError>,
    Scheduler {

    override var error: AlarmError? = null

    override fun addOrUpdateReminder(reminder: Reminder): Result<Unit, AlarmError> {
        return handleError {
            val isExistingReminderIndex = reminders.indexOfFirst { it.id == reminder.id }
            if (isExistingReminderIndex != -1) reminders[isExistingReminderIndex] = reminder
            else reminders.add(reminder)

            updateReminderState(result = Result.Success(data = reminders))
            Result.Success(data = Unit)
        }
    }

    override fun cancelReminder(id: Long): Result<Unit, AlarmError> {
        return handleError {
            val isExistingReminderIndex = reminders.indexOfFirst { it.id == id }
            if (isExistingReminderIndex != -1) {
                reminders.removeAt(isExistingReminderIndex)
                updateReminderState(result = Result.Success(data = reminders))
                Result.Success(data = Unit)
            } else {
                updateReminderState(result = Result.Error(error = AlarmError.ILLEGAL_ARGUMENT))
                Result.Error(error = AlarmError.ILLEGAL_ARGUMENT)
            }
        }
    }
}