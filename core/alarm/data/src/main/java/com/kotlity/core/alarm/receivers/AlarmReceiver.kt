package com.kotlity.core.alarm.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kotlity.core.alarm.util.doAsync
import com.kotlity.core.alarm.Scheduler
import com.kotlity.core.local.ReminderDao
import com.kotlity.core.Periodicity
import com.kotlity.core.Reminder
import com.kotlity.core.local.toReminderEntity
import com.kotlity.core.notification.NotificationService
import com.kotlity.core.util.DispatcherHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import kotlin.coroutines.CoroutineContext

class AlarmReceiver: BroadcastReceiver(), KoinComponent {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        val reminder = getReminderOrNull(context, intent) ?: return

        val scheduler by inject<Scheduler>()
        val reminderDao by inject<ReminderDao>()
        val notificationService by inject<NotificationService>()
        val dispatcherHandler by inject<DispatcherHandler>()


        val localDateTime = Instant.ofEpochMilli(reminder.reminderTime).atZone(ZoneId.systemDefault()).toLocalDateTime()

        if (shouldSendNotification(localDateTime, reminder)) notificationService.sendNotification(reminder.id.hashCode(), reminder.title)

        synchronizeUserReminder(
            appScope = GlobalScope,
            coroutineContext = dispatcherHandler.io,
            localDateTime = localDateTime,
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
        localDateTime: LocalDateTime,
        reminder: Reminder,
        reminderDao: ReminderDao,
        scheduler: Scheduler
    ) {
        doAsync(appScope, coroutineContext) {
            val periodicityTimestamp = periodicityTimestamp(localDateTime, reminder.periodicity)
            if (periodicityTimestamp != null) {
                val updatedReminder = reminder.copy(reminderTime = periodicityTimestamp)
                reminderDao.updateReminder(entity = updatedReminder.toReminderEntity())
                scheduler.addOrUpdateReminder(updatedReminder)
            }
            else {
                reminderDao.deleteReminder(reminder.id)
                scheduler.cancelReminder(reminder.id)
            }
        }
    }

    private fun shouldSendNotification(localDateTime: LocalDateTime, reminder: Reminder): Boolean {
        return when(reminder.periodicity) {
            Periodicity.ONCE, Periodicity.DAILY  -> true
            Periodicity.WEEKDAYS -> {
                val dayOfWeek = localDateTime.dayOfWeek
                dayOfWeek in DayOfWeek.MONDAY..DayOfWeek.FRIDAY
            }
        }
    }

    private fun periodicityTimestamp(localDateTime: LocalDateTime, periodicity: Periodicity): Long? {
        var updatedLocalDateTime = localDateTime
        when(periodicity) {
            Periodicity.ONCE -> return null
            Periodicity.DAILY -> updatedLocalDateTime = localDateTime.plusDays(1)
            Periodicity.WEEKDAYS -> {
                val dayOfWeek = localDateTime.dayOfWeek
                updatedLocalDateTime = when(dayOfWeek) {
                    DayOfWeek.FRIDAY -> localDateTime.plusDays(3)
                    DayOfWeek.SATURDAY -> localDateTime.plusDays(2)
                    else -> localDateTime.plusDays(1)
                }
            }
        }
        return updatedLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}