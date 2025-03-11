package com.kotlity.core.di

import com.kotlity.core.Periodicity
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ClockValidator
import com.kotlity.core.util.ValidatorQualifiers.DATE_VALIDATOR_QUALIFIER
import com.kotlity.core.validators.AlarmReminderDateValidator
import org.koin.core.qualifier.named
import org.koin.dsl.module

val alarmReminderDateValidatorModule = module {
    single<ClockValidator<Periodicity, Long, AlarmValidationError.AlarmReminderDateValidation>>(named(DATE_VALIDATOR_QUALIFIER)) { AlarmReminderDateValidator() }
}