package com.kotlity.feature_reminders

import com.kotlity.core.alarm.Scheduler
import com.kotlity.core.local.ReminderDao
import com.kotlity.core.local.toReminder
import com.kotlity.core.local.toReminderEntity
import com.kotlity.core.local.util.databaseCall
import com.kotlity.core.local.util.databaseFlowCall
import com.kotlity.core.Reminder
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.DispatcherHandler
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result
import kotlinx.coroutines.flow.Flow

class RemindersRepositoryImplementation(
    private val reminderDao: ReminderDao,
    private val alarmScheduler: Scheduler,
    private val dispatcherHandler: DispatcherHandler
): RemindersRepository {

    override fun getAllReminders(): Flow<Result<List<Reminder>, DatabaseError>> {
        return databaseFlowCall(
            dispatcher = dispatcherHandler.io,
            flowProvider = { reminderDao.getAllReminders() },
            mapper = { reminderEntities ->
                reminderEntities.map { it.toReminder() }
            }
        )
    }

    override suspend fun deleteReminder(id: Long): Result<Reminder?, ReminderError> {
        val alarmResult = alarmScheduler.cancelReminder(id)
        if (alarmResult is Result.Error) return Result.Error(ReminderError.Alarm(alarmResult.error))
        var deletedReminder: Reminder? = null
        val databaseResult = databaseCall(dispatcherHandler.io) {
            deletedReminder = reminderDao.getReminderById(id)?.toReminder()
            reminderDao.deleteReminder(id)
            Result.Success(Unit)
        }
        if (databaseResult is Result.Error) return Result.Error(ReminderError.Database(databaseResult.error))
        return Result.Success(deletedReminder)
    }

    override suspend fun restoreReminder(reminder: Reminder): Result<Unit, ReminderError> {
        val alarmResult = alarmScheduler.addOrUpdateReminder(reminder)
        if (alarmResult is Result.Error) return Result.Error(ReminderError.Alarm(alarmResult.error))
        val databaseResult = databaseCall(dispatcherHandler.io) {
            reminderDao.upsertReminder(reminder.toReminderEntity())
            Result.Success(Unit)
        }
        if (databaseResult is Result.Error) return Result.Error(ReminderError.Database(databaseResult.error))
        return Result.Success(Unit)
    }
}