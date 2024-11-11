package com.kotlity.core.alarm.domain

import com.kotlity.core.domain.util.AlarmError
import com.kotlity.core.domain.util.Result

interface Scheduler {

    fun addReminder(reminder: Reminder): Result<Unit, AlarmError>

    fun updateReminder(reminder: Reminder): Result<Unit, AlarmError>

    fun cancelReminder(id: Long): Result<Unit, AlarmError>
}