package com.kotlity.feature_reminders.data.di

import com.kotlity.feature_reminders.data.RemindersRepositoryImplementation
import com.kotlity.feature_reminders.domain.RemindersRepository
import org.koin.dsl.module

val remindersRepositoryModule = module {
    single<RemindersRepository> { RemindersRepositoryImplementation(get(), get(), get()) }
}