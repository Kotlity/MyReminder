package com.kotlity.feature_reminders.presentation.di

import com.kotlity.core.domain.util.DispatcherHandler
import com.kotlity.feature_reminders.presentation.TestDispatcherHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
val testDispatcherHandlerModule = module {
    val testDispatcher = UnconfinedTestDispatcher()
    single<DispatcherHandler> { TestDispatcherHandler(testDispatcher) }
}