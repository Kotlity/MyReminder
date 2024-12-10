package com.kotlity.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReminder(reminderEntity: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminderEntity: ReminderEntity)

    @Query("DELETE FROM ReminderEntity WHERE id = :id")
    suspend fun deleteReminder(id: Long)

    @Query("SELECT * FROM ReminderEntity WHERE id = :id")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Query("SELECT * FROM ReminderEntity")
    fun getAllReminders(): Flow<List<ReminderEntity>>
}