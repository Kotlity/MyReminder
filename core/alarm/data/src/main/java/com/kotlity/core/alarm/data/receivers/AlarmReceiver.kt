package com.kotlity.core.alarm.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kotlity.core.alarm.data.util.doAsync
import com.kotlity.core.alarm.domain.Scheduler
import com.kotlity.core.data.local.ReminderDao
import com.kotlity.core.domain.Periodicity
import com.kotlity.core.domain.Reminder
import com.kotlity.core.notification.domain.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar
import kotlin.coroutines.CoroutineContext

class AlarmReceiver: BroadcastReceiver(), KoinComponent {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        val reminder = getReminderOrNull(context, intent) ?: return

        val scheduler by inject<Scheduler>()
        val reminderDao by inject<ReminderDao>()
        val notificationService by inject<NotificationService>()

        val calendar = Calendar.getInstance().apply {
            timeInMillis = reminder.reminderTime
        }

        if (shouldSendNotification(calendar, reminder)) notificationService.sendNotification(reminder.id.hashCode(), reminder.title)

        synchronizeUserReminder(
            appScope = GlobalScope,
            coroutineContext = Dispatchers.IO,
            calendar = calendar,
            reminder = reminder,
            reminderDao = reminderDao,
            scheduler = scheduler
        )
    }

    private fun getReminderOrNull(context: Context, intent: Intent): Reminder? {
        val reminderBundle = intent.getBundleExtra(context.getString(com.kotlity.core.resources.R.string.reminderBundleExtraKey)) ?: return null

        val id = reminderBundle.getLong(context.getString(com.kotlity.core.resources.R.string.reminderIdExtraKey), -1)
        val title = reminderBundle.getString(context.getString(com.kotlity.core.resources.R.string.reminderTitleExtraKey), "")
        val time = reminderBundle.getLong(context.getString(com.kotlity.core.resources.R.string.reminderTimeExtraKey), -1)
        val periodicityName = reminderBundle.getString(context.getString(com.kotlity.core.resources.R.string.reminderPeriodicityExtraKey), "")
        val periodicity = Periodicity.valueOf(periodicityName)

        return Reminder(
            id = id,
            title = title,
            reminderTime = time,
            periodicity = periodicity
        )
    }

    private fun BroadcastReceiver.synchronizeUserReminder(
        appScope: CoroutineScope,
        coroutineContext: CoroutineContext,
        calendar: Calendar,
        reminder: Reminder,
        reminderDao: ReminderDao,
        scheduler: Scheduler
    ) {
        doAsync(appScope, coroutineContext) {
            val periodicityTimestamp = periodicityTimestamp(calendar, reminder.periodicity)
            if (periodicityTimestamp != null) scheduler.addOrUpdateReminder(reminder.copy(reminderTime = periodicityTimestamp))
            else {
                reminderDao.deleteReminder(reminder.id)
                scheduler.cancelReminder(reminder.id)
            }
        }
    }

    private fun shouldSendNotification(calendar: Calendar, reminder: Reminder): Boolean {
        return when(reminder.periodicity) {
            Periodicity.ONCE, Periodicity.DAILY  -> true
            Periodicity.WEEKDAYS -> {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY
            }
        }
    }

    private fun periodicityTimestamp(calendar: Calendar, periodicity: Periodicity): Long? {
        when(periodicity) {
            Periodicity.ONCE -> return null
            Periodicity.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            Periodicity.WEEKDAYS -> {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                when(dayOfWeek) {
                    Calendar.FRIDAY -> calendar.add(Calendar.DAY_OF_YEAR, 3)
                    Calendar.SATURDAY -> calendar.add(Calendar.DAY_OF_YEAR, 2)
                    else -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }
        return calendar.timeInMillis
    }
}