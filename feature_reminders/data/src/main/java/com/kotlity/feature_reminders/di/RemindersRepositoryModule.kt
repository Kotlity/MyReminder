package com.kotlity.feature_reminders.di

import com.kotlity.feature_reminders.RemindersRepositoryImplementation
import com.kotlity.feature_reminders.RemindersRepository
import org.koin.dsl.module

val remindersRepositoryModule = module {
    factory<RemindersRepository> { RemindersRepositoryImplementation(get(), get(), get()) }
}