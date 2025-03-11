package com.kotlity.core.di

import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ClockValidator
import com.kotlity.core.util.ValidatorQualifiers.TIME_VALIDATOR_QUALIFIER
import com.kotlity.core.validators.AlarmReminderTimeValidator
import org.koin.core.qualifier.named
import org.koin.dsl.module

val alarmReminderTimeValidatorModule = module {
    single<ClockValidator<Pair<Int, Int>, Long, AlarmValidationError.AlarmReminderTimeValidation>>(named(TIME_VALIDATOR_QUALIFIER)) { AlarmReminderTimeValidator() }
}