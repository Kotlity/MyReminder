package com.kotlity.core.di

import com.kotlity.TimeFormatter
import com.kotlity.core.DefaultTimeFormatter
import org.koin.dsl.module

val timeFormatterModule = module {
    single<TimeFormatter> { DefaultTimeFormatter(get()) }
}