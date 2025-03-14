package com.kotlity.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kotlity.core.local.Constants.REMINDER_DATABASE_VERSION

@Database(
    entities = [ReminderEntity::class],
    version = REMINDER_DATABASE_VERSION
)
@TypeConverters(value = [PeriodicityConverter::class])
abstract class ReminderDatabase: RoomDatabase() {

    abstract val reminderDao: ReminderDao
}