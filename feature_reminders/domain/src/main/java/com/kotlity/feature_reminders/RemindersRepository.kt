package com.kotlity.feature_reminders

import com.kotlity.core.Reminder
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result
import kotlinx.coroutines.flow.Flow

interface RemindersRepository {

    fun getAllReminders(): Flow<Result<List<Reminder>, DatabaseError>>

    suspend fun deleteReminder(id: Long): Result<Reminder?, ReminderError>

    suspend fun restoreReminder(reminder: Reminder): Result<Unit, ReminderError>
}