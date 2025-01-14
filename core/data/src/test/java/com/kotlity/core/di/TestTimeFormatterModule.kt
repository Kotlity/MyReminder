package com.kotlity.core.di

import com.kotlity.TimeFormatter
import com.kotlity.core.TestTimeFormatter
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val testTimeFormatterModule = module {
    factoryOf(::TestTimeFormatter) { bind<TimeFormatter>() }
}