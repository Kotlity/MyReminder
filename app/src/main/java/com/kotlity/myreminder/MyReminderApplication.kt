package com.kotlity.myreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.kotlity.core.alarm.di.alarmManagerModule
import com.kotlity.core.alarm.di.alarmSchedulerModule
import com.kotlity.core.di.alarmReminderDateValidatorModule
import com.kotlity.core.di.alarmReminderTimeValidatorModule
import com.kotlity.core.di.alarmReminderTitleValidatorModule
import com.kotlity.core.di.dispatcherHandlerModule
import com.kotlity.core.di.timeFormatterModule
import com.kotlity.core.local.di.reminderDaoModule
import com.kotlity.core.local.di.reminderDatabaseModule
import com.kotlity.core.notification.di.notificationManagerModule
import com.kotlity.core.notification.di.notificationServiceModule
import com.kotlity.feature_reminder_editor.di.reminderEditorRepositoryModule
import com.kotlity.feature_reminder_editor.di.reminderEditorViewModelModule
import com.kotlity.feature_reminders.di.remindersRepositoryModule
import com.kotlity.feature_reminders.di.remindersViewModelModule
import com.kotlity.permissions.di.permissionsManagerModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyReminderApplication: Application() {

    private val notificationManager by inject<NotificationManager>()

    override fun onCreate() {
        super.onCreate()
        setupKoin()
        setupNotificationChannel(notificationManager)
    }
}

private fun Application.setupKoin() {
    startKoin {
        androidContext(this@setupKoin)
        androidLogger()
        modules(
            notificationManagerModule,
            notificationServiceModule,
            alarmManagerModule,
            alarmSchedulerModule,
            alarmReminderTitleValidatorModule,
            alarmReminderTimeValidatorModule,
            alarmReminderDateValidatorModule,
            reminderDatabaseModule,
            reminderDaoModule,
            timeFormatterModule,
            dispatcherHandlerModule,
            remindersRepositoryModule,
            remindersViewModelModule,
            permissionsManagerModule,
            reminderEditorRepositoryModule,
            reminderEditorViewModelModule
        )
    }
}

private fun Application.setupNotificationChannel(notificationManager: NotificationManager) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val notificationChannelId = getString(com.kotlity.core.resources.R.string.notificationChannelId)
    val notificationChannelName = getString(com.kotlity.core.resources.R.string.notificationChannelName)
    val importance = NotificationManager.IMPORTANCE_HIGH
    val notificationChannelDescription = getString(com.kotlity.core.resources.R.string.notificationChannelDescription)
    val notificationChannel = NotificationChannel(
        notificationChannelId,
        notificationChannelName,
        importance
    ).apply { description = notificationChannelDescription }
    notificationManager.createNotificationChannel(notificationChannel)
}