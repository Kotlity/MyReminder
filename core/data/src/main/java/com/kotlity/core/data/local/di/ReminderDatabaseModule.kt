package com.kotlity.core.data.local.di

import androidx.room.Room
import com.kotlity.core.data.local.ReminderDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val reminderDatabaseModule = module {
    single {
        val databaseName = androidContext().getString(com.kotlity.core.resources.R.string.reminderDatabaseName)
        Room.databaseBuilder(androidContext(), ReminderDatabase::class.java, databaseName)
            .fallbackToDestructiveMigration()
            .build()
    }
}