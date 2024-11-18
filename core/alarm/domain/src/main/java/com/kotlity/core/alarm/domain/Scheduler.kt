package com.kotlity.core.alarm.domain

import com.kotlity.core.domain.Reminder
import com.kotlity.core.domain.util.AlarmError
import com.kotlity.core.domain.util.Result

interface Scheduler {

    fun addOrUpdateReminder(reminder: Reminder): Result<Unit, AlarmError>

    fun cancelReminder(id: Long): Result<Unit, AlarmError>
}