package com.kotlity.myreminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.kotlity.core.alarm.data.di.alarmManagerModule
import com.kotlity.core.alarm.data.di.alarmSchedulerModule
import com.kotlity.core.data.di.dispatcherHandlerModule
import com.kotlity.core.data.local.di.reminderDaoModule
import com.kotlity.core.data.local.di.reminderDatabaseModule
import com.kotlity.core.notification.data.di.notificationManagerModule
import com.kotlity.core.notification.data.di.notificationServiceModule
import com.kotlity.feature_reminders.data.di.remindersRepositoryModule
import com.kotlity.feature_reminders.presentation.di.remindersViewModelModule
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
            reminderDatabaseModule,
            reminderDaoModule,
            dispatcherHandlerModule,
            remindersRepositoryModule,
            remindersViewModelModule
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