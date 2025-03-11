package com.kotlity.core.alarm.receivers

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kotlity.core.alarm.util.doAsync
import com.kotlity.core.alarm.Scheduler
import com.kotlity.core.local.ReminderDao
import com.kotlity.core.local.toReminder
import com.kotlity.core.util.DispatcherHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExactAlarmPermissionReceiver: BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context?, intent: Intent?) {
        recreatingAlarmsIfNeeded(intent)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun recreatingAlarmsIfNeeded(intent: Intent?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        if (intent?.action != "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED") return

        val alarmManager by inject<AlarmManager>()
        val scheduler by inject<Scheduler>()
        val reminderDao by inject<ReminderDao>()
        val dispatcherHandler by inject<DispatcherHandler>()

        doAsync(
            GlobalScope,
            dispatcherHandler.io
        ) {
            val reminders = reminderDao.getAllReminders().firstOrNull()
            val isRecreatingAlarmsNeeded = alarmManager.canScheduleExactAlarms() && reminders != null
            if (!isRecreatingAlarmsNeeded) return@doAsync

            reminders!!
                .map { reminderEntity -> reminderEntity.toReminder() }
                .forEach { reminder -> scheduler.addOrUpdateReminder(reminder) }
        }
    }
}