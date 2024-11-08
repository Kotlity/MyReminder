package com.kotlity.core.data.local.di

import com.kotlity.core.data.local.ReminderDatabase
import org.koin.dsl.module

val reminderDaoModule = module {
    single { get<ReminderDatabase>().reminderDao }
}