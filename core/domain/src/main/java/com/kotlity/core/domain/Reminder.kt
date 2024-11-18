package com.kotlity.core.domain

data class Reminder(
    val id: Long,
    val title: String,
    val reminderTime: Long,
    val periodicity: Periodicity
)