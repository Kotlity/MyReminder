package com.kotlity.feature_reminder_editor.models

import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ValidationStatus

data class ValidationStatuses(
    val title: ValidationStatus<AlarmValidationError.AlarmTitleValidation>,
    val time: ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation>,
    val date: ValidationStatus<AlarmValidationError.AlarmReminderDateValidation>
)