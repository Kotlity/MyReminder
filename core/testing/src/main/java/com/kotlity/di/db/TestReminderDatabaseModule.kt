package com.kotlity.di.db

import androidx.room.Room
import com.kotlity.core.local.ReminderDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val testReminderDatabaseModule = module {
    single {
        Room.inMemoryDatabaseBuilder(androidContext(), ReminderDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}