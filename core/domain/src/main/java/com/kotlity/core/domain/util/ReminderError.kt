package com.kotlity.core.domain.util

sealed interface ReminderError: Error {
    data class Database(val error: DatabaseError): ReminderError
    data class Alarm(val error: AlarmError): ReminderError
}