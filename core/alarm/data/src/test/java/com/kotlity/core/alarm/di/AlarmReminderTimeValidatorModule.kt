package com.kotlity.core.alarm.di

import com.kotlity.core.alarm.validators.AlarmReminderTimeValidator
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.Validator
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val alarmReminderTimeValidatorModule = module {
    singleOf(::AlarmReminderTimeValidator) { bind<Validator<Long, AlarmValidationError.AlarmReminderTimeValidation>>() }
}