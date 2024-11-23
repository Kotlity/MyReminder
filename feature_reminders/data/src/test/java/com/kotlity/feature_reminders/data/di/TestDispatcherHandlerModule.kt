package com.kotlity.feature_reminders.data.di

import com.kotlity.core.domain.util.DispatcherHandler
import com.kotlity.feature_reminders.data.TestDispatcherHandler
import org.koin.dsl.module

val testDispatcherHandlerModule = module {
    single<DispatcherHandler> { TestDispatcherHandler() }
}