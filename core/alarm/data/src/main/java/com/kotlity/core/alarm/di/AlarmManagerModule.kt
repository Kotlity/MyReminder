package com.kotlity.core.alarm.di

import android.app.AlarmManager
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val alarmManagerModule = module {
    single { androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
}