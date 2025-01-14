package com.kotlity.di

import com.kotlity.TestTimeFormatter
import com.kotlity.TimeFormatter
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val testTimeFormatterModule = module {
    factoryOf(::TestTimeFormatter) { bind<TimeFormatter>() }
}