package com.kotlity.feature_reminder_editor.di

import com.kotlity.feature_reminder_editor.TestReminderEditorRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val testReminderEditorRepositoryModule = module {
    factoryOf(::TestReminderEditorRepository)
}