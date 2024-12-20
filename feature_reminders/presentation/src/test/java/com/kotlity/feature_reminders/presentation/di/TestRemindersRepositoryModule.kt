package com.kotlity.feature_reminders.presentation.di

import com.kotlity.feature_reminders.presentation.TestRemindersRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val testRemindersRepositoryModule = module {
    factoryOf(::TestRemindersRepository)
}