package com.kotlity.feature_reminder_editor.composables.title

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import com.kotlity.core.ResourcesConstant._18sp
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.red
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.toString
import com.kotlity.feature_reminder_editor.composables.EditorTextField

@Composable
internal fun EditorTitleTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    singleLine: Boolean = true,
    hint: String?,
    hintStyle: TextStyle = MaterialTheme.typography.displaySmall,
    textStyle: TextStyle = hintStyle.copy(fontSize = _18sp),
    errorText: String? = null,
    isError: Boolean,
    onFocusChange: (Boolean) -> Unit
) {

    Column(modifier = modifier) {
        EditorTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            onTextChange = onTextChange,
            singleLine = singleLine,
            hintStyle = hintStyle,
            textStyle = textStyle,
            isError = isError,
            hint = hint,
            onFocusChange = onFocusChange
        )
        if (isError) {
            Text(
                text = errorText!!,
                style = hintStyle.copy(color = red),
                modifier = Modifier.padding(top = dimensionResource(id = dimen._4dp))
            )
        }
    }

}

@PreviewAnnotation
@Composable
private fun EditorTitleTextFieldPreview() {

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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EditorTitleTextField(
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
}