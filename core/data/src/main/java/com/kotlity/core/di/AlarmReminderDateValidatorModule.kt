package com.kotlity.core.di

import com.kotlity.core.Periodicity
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ClockValidator
import com.kotlity.core.validators.AlarmReminderDateValidator
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val alarmReminderDateValidatorModule = module {
    singleOf(::AlarmReminderDateValidator) { bind<ClockValidator<Periodicity, Long, AlarmValidationError.AlarmReminderDateValidation>>() }
}