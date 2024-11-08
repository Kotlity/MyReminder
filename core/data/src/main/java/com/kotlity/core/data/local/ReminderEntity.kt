package com.kotlity.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kotlity.core.domain.Periodicity

@Entity
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String,
    val reminderTime: Long,
    val periodicity: Periodicity
)