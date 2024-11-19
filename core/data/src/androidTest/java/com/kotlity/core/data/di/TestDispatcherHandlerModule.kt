package com.kotlity.core.data.di

import com.kotlity.core.data.TestDispatcherHandler
import com.kotlity.core.domain.util.DispatcherHandler
import org.koin.dsl.module

val testDispatcherHandlerModule = module {
    single<DispatcherHandler> { TestDispatcherHandler() }
}