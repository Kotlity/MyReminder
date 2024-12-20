package com.kotlity.core.notification.data.di

import com.kotlity.core.notification.data.DefaultNotificationService
import com.kotlity.core.notification.domain.NotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationServiceModule = module {
    factory<NotificationService> { DefaultNotificationService(get(), androidContext()) }
}