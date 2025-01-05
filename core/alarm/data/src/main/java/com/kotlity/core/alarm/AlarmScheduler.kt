package com.kotlity.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.os.bundleOf
import com.kotlity.core.alarm.receivers.AlarmReceiver
import com.kotlity.core.alarm.util.reminderCall
import com.kotlity.core.Reminder
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.Result
import com.kotlity.core.resources.R

class AlarmScheduler(
    private val alarmManager: AlarmManager,
    private val context: Context
): Scheduler {
    private val canScheduleExactAlarms: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms() else true

    override fun addOrUpdateReminder(reminder: Reminder): Result<Unit, AlarmError> {
        return reminderCall {
            if (!canScheduleExactAlarms) return@reminderCall Result.Error(error = AlarmError.SECURITY)
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                val bundle = bundleOf(
                    context.getString(R.string.reminderIdExtraKey) to reminder.id,
                    context.getString(R.string.reminderTitleExtraKey) to reminder.title,
                    context.getString(R.string.reminderTimeExtraKey) to reminder.reminderTime,
                    context.getString(R.string.reminderPeriodicityExtraKey) to reminder.periodicity.name
                )
                putExtra(context.getString(R.string.reminderBundleExtraKey), bundle)
            }
            val pendingIntent = PendingIntent.getBroadcast(context, reminder.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.reminderTime, pendingIntent)
            Result.Success(Unit)
        }
    }

    override fun cancelReminder(id: Long): Result<Unit, AlarmError> {
        return reminderCall {
            if (!canScheduleExactAlarms) return@reminderCall Result.Error(error = AlarmError.SECURITY)
            val pendingIntent = Intent(context, AlarmReceiver::class.java).let {
                PendingIntent.getBroadcast(context, id.hashCode(), it, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            }
            alarmManager.cancel(pendingIntent)
            Result.Success(Unit)
        }
    }
}