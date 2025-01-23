package com.kotlity.core.alarm.di

import com.kotlity.core.alarm.TestScheduler
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val testSchedulerModule = module {
    factory { TestScheduler() }
}