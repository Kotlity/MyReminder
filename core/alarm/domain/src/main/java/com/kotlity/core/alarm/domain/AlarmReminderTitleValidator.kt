package com.kotlity.core.alarm.domain

import com.kotlity.core.domain.util.AlarmValidationError
import com.kotlity.core.domain.util.Result

private const val MAX_TITLE_LENGTH = 30

class AlarmReminderTitleValidator {

    operator fun invoke(title: String): Result<Unit, AlarmValidationError.AlarmTitleValidation> {
        if (title.isBlank()) return Result.Error(error = AlarmValidationError.AlarmTitleValidation.BLANK)
        if (title.first().isLowerCase()) return Result.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_LOWERCASE)
        if (title.first().isDigit()) return Result.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_DIGIT)
        if (title.length > MAX_TITLE_LENGTH) return Result.Error(error = AlarmValidationError.AlarmTitleValidation.TOO_LONG)
        else return Result.Success(Unit)
    }
}