package com.kotlity.feature_reminder_editor.composables.title

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.toString
import com.kotlity.feature_reminder_editor.composables.EditorTitleSection

@Composable
internal fun TitleSection(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    text: String,
    onTextChange: (String) -> Unit,
    hint: String?,
    errorText: String? = null,
    isError: Boolean,
    onFocusChange: (Boolean) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        EditorTitleSection(
            titleRes = string.title,
            iconRes = drawable.write,
            iconContentDescription = stringResource(id = string.titleIconDescription)
        )
        EditorTitleTextField(
            text = text,
            onTextChange = onTextChange,
            hint = hint,
            errorText = errorText,
            isError = isError,
            onFocusChange = onFocusChange
        )
    }
}

@PreviewAnnotation
@Composable
private fun TitleSectionPreview() {

    val context = LocalContext.current

    var text by remember {
        mutableStateOf("")
    }

    val hint = "Insert Title"

    val validationStatus: ValidationStatus<AlarmValidationError.AlarmTitleValidation> by remember {
        derivedStateOf {
            if (text.isEmpty()) return@derivedStateOf ValidationStatus.Unspecified
            if (text.isBlank()) return@derivedStateOf ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.BLANK)
            if (text.first().isLowerCase()) return@derivedStateOf ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_LOWERCASE)
            if (text.first().isDigit()) return@derivedStateOf ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_DIGIT)
            if (text.length > 30) return@derivedStateOf ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.TOO_LONG)
            else ValidationStatus.Success
        }
    }

    MyReminderTheme {
        TitleSection(
            modifier = Modifier.fillMaxWidth(0.7f),
            text = text,
            onTextChange = {
                text = it
            },
            hint = if (validationStatus.isUnspecified()) hint else null,
            errorText = if (validationStatus.isError()) validationStatus.getValidationError().toString(context = context) else null,
            isError = validationStatus.isError(),
            onFocusChange = { }
        )
    }
}