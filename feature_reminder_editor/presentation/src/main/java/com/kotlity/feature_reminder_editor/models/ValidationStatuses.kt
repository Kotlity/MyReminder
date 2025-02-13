package com.kotlity.feature_reminder_editor.models

import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.ValidationStatus

internal data class ValidationStatuses(
    val title: ValidationStatus<AlarmValidationError.AlarmTitleValidation> = ValidationStatus.Unspecified,
    val time: ValidationStatus<AlarmValidationError.AlarmReminderTimeValidation> = ValidationStatus.Unspecified,
    val date: ValidationStatus<AlarmValidationError.AlarmReminderDateValidation> = ValidationStatus.Unspecified
)