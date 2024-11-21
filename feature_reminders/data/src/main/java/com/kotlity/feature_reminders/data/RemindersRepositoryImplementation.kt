package com.kotlity.feature_reminders.data

import com.kotlity.core.alarm.domain.Scheduler
import com.kotlity.core.data.local.ReminderDao
import com.kotlity.core.data.local.toReminder
import com.kotlity.core.data.local.util.databaseCall
import com.kotlity.core.data.local.util.databaseFlowCall
import com.kotlity.core.domain.Reminder
import com.kotlity.core.domain.util.DatabaseError
import com.kotlity.core.domain.util.DispatcherHandler
import com.kotlity.core.domain.util.ReminderError
import com.kotlity.core.domain.util.Result
import com.kotlity.feature_reminders.domain.RemindersRepository
import kotlinx.coroutines.flow.Flow

class RemindersRepositoryImplementation(
    private val reminderDao: ReminderDao,
    private val alarmScheduler: Scheduler,
    private val dispatcherHandler: DispatcherHandler
): RemindersRepository {

    override fun getAllReminders(): Flow<Result<List<Reminder>, DatabaseError>> {
        return databaseFlowCall(dispatcherHandler.io, reminderDao.getAllReminders()) { reminderEntities ->
            if (reminderEntities.isEmpty()) Result.Success(emptyList())
            else {
                val reminders = reminderEntities.map { it.toReminder() }
                Result.Success(reminders)
            }
        }
    }

    override suspend fun deleteReminder(id: Long): Result<Unit, ReminderError> {
        val databaseResult = databaseCall(dispatcherHandler.io) {
            reminderDao.deleteReminder(id)
            Result.Success(Unit)
        }
        if (databaseResult is Result.Error) return Result.Error(ReminderError.Database(databaseResult.error))
        val result = alarmScheduler.cancelReminder(id)
        if (result is Result.Error) return Result.Error(ReminderError.Alarm(result.error))
        return Result.Success(Unit)
    }
}