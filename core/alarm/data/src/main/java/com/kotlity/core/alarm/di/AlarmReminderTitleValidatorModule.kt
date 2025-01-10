package com.kotlity.core.alarm.di

import com.kotlity.core.alarm.validators.AlarmReminderTitleValidator
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.Validator
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val alarmReminderTitleValidatorModule = module {
    singleOf(::AlarmReminderTitleValidator) { bind<Validator<String, AlarmValidationError.AlarmTitleValidation>>() }
}