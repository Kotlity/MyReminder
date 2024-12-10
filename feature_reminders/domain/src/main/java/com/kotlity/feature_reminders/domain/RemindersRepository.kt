package com.kotlity.feature_reminders.domain

import com.kotlity.core.domain.Reminder
import com.kotlity.core.domain.util.DatabaseError
import com.kotlity.core.domain.util.ReminderError
import com.kotlity.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface RemindersRepository {

    fun getAllReminders(): Flow<Result<List<Reminder>, DatabaseError>>

    suspend fun deleteReminder(id: Long): Result<Reminder?, ReminderError>

    suspend fun restoreReminder(reminder: Reminder): Result<Unit, ReminderError>
}