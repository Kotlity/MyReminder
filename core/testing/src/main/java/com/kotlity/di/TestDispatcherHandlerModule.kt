package com.kotlity.di

import com.kotlity.core.util.DispatcherHandler
import com.kotlity.utils.TestDispatcherHandler
import org.koin.dsl.module

val testDispatcherHandlerModule = module {
    single<DispatcherHandler> { TestDispatcherHandler() }
}