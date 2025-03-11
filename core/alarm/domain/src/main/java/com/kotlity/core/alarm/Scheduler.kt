package com.kotlity.core.alarm

import com.kotlity.core.Reminder
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.Result

interface Scheduler {

    val canScheduleAlarms: Boolean

    fun addOrUpdateReminder(reminder: Reminder): Result<Unit, AlarmError>

    fun cancelReminder(id: Long): Result<Unit, AlarmError>
}