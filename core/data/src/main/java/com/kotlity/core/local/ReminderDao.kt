package com.kotlity.core.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(entity: ReminderEntity): Long

    @Update
    suspend fun updateReminder(entity: ReminderEntity)

    @Upsert
    suspend fun upsertReminder(entity: ReminderEntity)

    @Query("DELETE FROM ReminderEntity WHERE id = :id")
    suspend fun deleteReminder(id: Long)

    @Query("SELECT * FROM ReminderEntity WHERE id = :id")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Query("SELECT * FROM ReminderEntity ORDER BY reminderTime ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>
}