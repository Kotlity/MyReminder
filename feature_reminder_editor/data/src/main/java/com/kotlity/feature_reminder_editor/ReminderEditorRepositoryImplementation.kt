package com.kotlity.feature_reminder_editor

import com.kotlity.core.Reminder
import com.kotlity.core.alarm.Scheduler
import com.kotlity.core.local.ReminderDao
import com.kotlity.core.local.toReminder
import com.kotlity.core.local.toReminderEntity
import com.kotlity.core.local.util.databaseCall
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.DispatcherHandler
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result

class ReminderEditorRepositoryImplementation(
    private val alarmScheduler: Scheduler,
    private val reminderDao: ReminderDao,
    private val dispatcherHandler: DispatcherHandler
): ReminderEditorRepository {

    override suspend fun getReminderById(id: Long): Result<Reminder?, DatabaseError> {
        return databaseCall(
            dispatcher = dispatcherHandler.io,
            block = {
                val reminder = reminderDao.getReminderById(id = id)?.toReminder()
                Result.Success(data = reminder)
            }
        )
    }

    override suspend fun upsertReminder(reminder: Reminder): Result<Unit, ReminderError> {
        if (!alarmScheduler.canScheduleAlarms) return Result.Error(error = ReminderError.Alarm(error = AlarmError.SECURITY))
        var id: Long = reminder.id

        val databaseResult = databaseCall(
            dispatcher = dispatcherHandler.io,
            block = {
                val entity = reminder.toReminderEntity()

                if (entity.id == null) id = reminderDao.insertReminder(entity = entity)
                else reminderDao.updateReminder(entity = entity)

                Result.Success(data = Unit)
            }
        )
        if (databaseResult is Result.Error) return Result.Error(error = ReminderError.Database(error = databaseResult.error))

        val updatedReminder = reminder.copy(id = id)

        val alarmResult = alarmScheduler.addOrUpdateReminder(reminder = updatedReminder)
        if (alarmResult is Result.Error) return Result.Error(error = ReminderError.Alarm(error = alarmResult.error))

        return Result.Success(data = Unit)
    }
}