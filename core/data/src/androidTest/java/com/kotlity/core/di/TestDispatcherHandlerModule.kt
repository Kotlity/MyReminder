package com.kotlity.core.di

import com.kotlity.core.TestDispatcherHandler
import com.kotlity.core.util.DispatcherHandler
import org.koin.dsl.module

val testDispatcherHandlerModule = module {
    single<DispatcherHandler> { TestDispatcherHandler() }
}