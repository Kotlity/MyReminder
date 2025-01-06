package com.kotlity.feature_reminder_editor

import com.kotlity.core.Reminder
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result

interface ReminderEditorRepository {

    suspend fun getReminderById(id: Long): Result<Reminder?, DatabaseError>

    suspend fun upsertReminder(reminder: Reminder): Result<Unit, ReminderError>
}