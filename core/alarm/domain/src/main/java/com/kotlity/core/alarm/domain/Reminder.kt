package com.kotlity.core.alarm.domain

import com.kotlity.core.domain.Periodicity

data class Reminder(
    val id: Long,
    val title: String,
    val reminderTime: Long,
    val periodicity: Periodicity
)