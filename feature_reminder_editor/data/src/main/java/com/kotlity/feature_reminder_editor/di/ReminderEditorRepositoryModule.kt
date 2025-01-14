package com.kotlity.feature_reminder_editor.di

import com.kotlity.feature_reminder_editor.ReminderEditorRepository
import com.kotlity.feature_reminder_editor.ReminderEditorRepositoryImplementation
import org.koin.dsl.module

val reminderEditorRepositoryModule = module {
    factory<ReminderEditorRepository> { ReminderEditorRepositoryImplementation(get(), get(), get()) }
}