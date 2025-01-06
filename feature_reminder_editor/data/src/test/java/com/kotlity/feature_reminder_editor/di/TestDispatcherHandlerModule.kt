package com.kotlity.feature_reminder_editor.di

import com.kotlity.core.util.DispatcherHandler
import com.kotlity.feature_reminder_editor.TestDispatcherHandler
import org.koin.dsl.module

val testDispatcherHandlerModule = module {
    single<DispatcherHandler> { TestDispatcherHandler() }
}