package com.kotlity.core.alarm.di

import com.kotlity.core.alarm.AlarmScheduler
import com.kotlity.core.alarm.Scheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val alarmSchedulerModule = module {
    factory<Scheduler> { AlarmScheduler(get(), androidContext()) }
}