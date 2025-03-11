package com.kotlity.feature_reminder_editor.di

import com.kotlity.core.util.ValidatorQualifiers.DATE_VALIDATOR_QUALIFIER
import com.kotlity.core.util.ValidatorQualifiers.TIME_VALIDATOR_QUALIFIER
import com.kotlity.feature_reminder_editor.ReminderEditorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val reminderEditorViewModelModule = module {
    viewModel {
        ReminderEditorViewModel(get(), get(), get(), get(), get(),
            get(named(TIME_VALIDATOR_QUALIFIER)),
            get(named(DATE_VALIDATOR_QUALIFIER))
        )
    }
}