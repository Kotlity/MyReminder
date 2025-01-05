package com.kotlity.core.di

import com.kotlity.core.DefaultDispatcherHandler
import com.kotlity.core.util.DispatcherHandler
import org.koin.dsl.module

val dispatcherHandlerModule = module {
    single<DispatcherHandler> { DefaultDispatcherHandler() }
}