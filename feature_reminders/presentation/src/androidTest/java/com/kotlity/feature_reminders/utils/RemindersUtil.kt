package com.kotlity.feature_reminders.utils

import com.kotlity.core.Periodicity
import com.kotlity.feature_reminders.mappers.toDisplayableReminderTime
import com.kotlity.feature_reminders.models.ReminderUi

object RemindersUtil {

    val mockReminders = (0..20).map { index ->
        val timeInMillis = System.currentTimeMillis() + index.toLong() * 10000
        val reminderTime = timeInMillis.toDisplayableReminderTime(is24HourFormat = true)
        ReminderUi(
            id = index.toLong(),
            title = "Title $index",
            reminderTime = reminderTime,
            periodicity = if (index % 2 == 0) Periodicity.WEEKDAYS else Periodicity.ONCE
        )
    }

    val firstReminderItem = mockReminders.first()

    val middleReminderItem = mockReminders[mockReminders.size / 2]

    val lastReminderItem = mockReminders.last()
}