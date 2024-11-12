package com.kotlity.core.alarm.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.kotlity.core.alarm.data.util.reminderCall
import com.kotlity.core.alarm.domain.Reminder
import com.kotlity.core.alarm.domain.Scheduler
import com.kotlity.core.domain.util.AlarmError
import com.kotlity.core.domain.util.Result

class AlarmScheduler(private val context: Context): Scheduler {

    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun addReminder(reminder: Reminder): Result<Unit, AlarmError> {
        return reminderCall {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(context.getString(com.kotlity.core.resources.R.string.reminderIdExtraKey), reminder.id)
                putExtra(context.getString(com.kotlity.core.resources.R.string.reminderTitleExtraKey), reminder.title)
                putExtra(context.getString(com.kotlity.core.resources.R.string.reminderTimeExtraKey), reminder.reminderTime)
                putExtra(context.getString(com.kotlity.core.resources.R.string.reminderPeriodicityExtraKey), reminder.periodicity)
            }
            val pendingIntent = PendingIntent.getBroadcast(context, reminder.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.reminderTime, pendingIntent)
            Result.Success(Unit)
        }
    }

    override fun updateReminder(reminder: Reminder): Result<Unit, AlarmError> {
        return reminderCall {
            val cancellingResult = cancelReminder(reminder.id)
            if (cancellingResult is Result.Error) return@reminderCall cancellingResult
            addReminder(reminder)
        }
    }

    override fun cancelReminder(id: Long): Result<Unit, AlarmError> {
        return reminderCall {
            val pendingIntent = Intent(context, AlarmReceiver::class.java).let {
                PendingIntent.getBroadcast(context, id.toInt(), it, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            }
            alarmManager.cancel(pendingIntent)
            Result.Success(Unit)
        }
    }
}