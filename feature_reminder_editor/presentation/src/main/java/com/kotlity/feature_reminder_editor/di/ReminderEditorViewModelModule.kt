package com.kotlity.feature_reminder_editor.di

import com.kotlity.feature_reminder_editor.ReminderEditorViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val reminderEditorViewModelModule = module {
    viewModelOf(::ReminderEditorViewModel)
}