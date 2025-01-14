package com.kotlity.di.db

import com.kotlity.core.local.ReminderDatabase
import org.koin.dsl.module

val testReminderDaoModule = module {
    single { get<ReminderDatabase>().reminderDao }
}