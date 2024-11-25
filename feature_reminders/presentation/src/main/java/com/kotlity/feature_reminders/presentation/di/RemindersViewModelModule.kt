package com.kotlity.feature_reminders.presentation.di

import com.kotlity.feature_reminders.presentation.RemindersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val remindersViewModelModule = module {
    viewModel { RemindersViewModel(get()) }
}