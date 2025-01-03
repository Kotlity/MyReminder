package com.kotlity.core.data.di

import com.kotlity.core.data.DefaultDispatcherHandler
import com.kotlity.core.domain.util.DispatcherHandler
import org.koin.dsl.module

val dispatcherHandlerModule = module {
    single<DispatcherHandler> { DefaultDispatcherHandler() }
}