package com.kotlity.core.alarm.data.di

import com.kotlity.core.alarm.data.AlarmScheduler
import com.kotlity.core.alarm.domain.Scheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val alarmSchedulerModule = module {
    single<Scheduler> { AlarmScheduler(get(), androidContext()) }
}