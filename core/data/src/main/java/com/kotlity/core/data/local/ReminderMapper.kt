package com.kotlity.core.data.local

import com.kotlity.core.domain.Reminder

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
        id = id,
        title = title,
        reminderTime = reminderTime,
        periodicity = periodicity
    )
}