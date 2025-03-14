package com.kotlity.core.local

import com.kotlity.core.Reminder

fun ReminderEntity.toReminder(): Reminder {
    return Reminder(
        id = id ?: 0,
        title = title,
        reminderTime = reminderTime,
        periodicity = periodicity
    )
}

fun Reminder.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        id = if (id == 0L) null else id,
        title = title,
        reminderTime = reminderTime,
        periodicity = periodicity
    )
}