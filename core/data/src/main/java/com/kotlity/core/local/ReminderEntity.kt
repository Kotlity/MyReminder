package com.kotlity.core.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kotlity.core.Periodicity

@Entity
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String,
    val reminderTime: Long,
    val periodicity: Periodicity
)