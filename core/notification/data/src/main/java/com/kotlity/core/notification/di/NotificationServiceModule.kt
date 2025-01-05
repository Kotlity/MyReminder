package com.kotlity.core.notification.di

import com.kotlity.core.notification.DefaultNotificationService
import com.kotlity.core.notification.NotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationServiceModule = module {
    factory<NotificationService> { DefaultNotificationService(get(), androidContext()) }
}