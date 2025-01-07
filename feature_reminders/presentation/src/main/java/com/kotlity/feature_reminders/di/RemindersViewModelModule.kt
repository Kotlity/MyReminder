package com.kotlity.feature_reminders.di

import com.kotlity.feature_reminders.RemindersViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val remindersViewModelModule = module {
    viewModelOf(::RemindersViewModel)
}