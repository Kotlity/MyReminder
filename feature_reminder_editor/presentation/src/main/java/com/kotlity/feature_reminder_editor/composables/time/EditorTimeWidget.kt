package com.kotlity.feature_reminder_editor.composables.time

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kotlity.core.ResourcesConstant._0_4
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.red
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.toString
import com.kotlity.feature_reminder_editor.composables.EditorTextField
import com.kotlity.feature_reminder_editor.models.DisplayableTimeResponse
import com.kotlity.feature_reminder_editor.utils.TextFieldHintProvider

@Composable
internal fun EditorTimeWidget(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = dimen._4dp)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    displayableResponse: DisplayableTimeResponse,
    isError: Boolean,
    errorText: String?,
    errorTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = red),
    @StringRes dividerRes: Int = string.timeDateDivider,
    onClick: () -> Unit
) {

    val context = LocalContext.current

    val textFieldsModifier = Modifier.width(IntrinsicSize.Min)
    val horizontalPadding = PaddingValues(horizontal = dimensionResource(id = dimen._5dp))

    val textFieldsHint = TimeTextFieldHintProvider(context = context).getHint(data = displayableResponse)

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = dimen._10dp)),
            verticalAlignment = Alignment.Bottom
        ) {
            EditorTextField(
                modifier = textFieldsModifier,
                text = displayableResponse.hour ?: "",
                onTextChange = {},
                enabled = false,
                isError = isError,
                hint = textFieldsHint,
                hintPadding = horizontalPadding,
                onClick = onClick
            )
            Text(text = stringResource(id = dividerRes))
            EditorTextField(
                modifier = textFieldsModifier,
                text = displayableResponse.minute ?: "",
                onTextChange = {},
                enabled = false,
                isError = isError,
                hint = textFieldsHint,
                hintPadding = horizontalPadding,
                onClick = onClick
            )
        }
        if (isError) {
            Text(
                modifier = Modifier.fillMaxWidth(_0_4),
                text = errorText!!,
                style = errorTextStyle
            )
        }
    }
}

private class TimeTextFieldHintProvider(private val context: Context): TextFieldHintProvider<DisplayableTimeResponse> {

    override fun getHint(data: DisplayableTimeResponse): String? {
        return if (data.hour == null && data.minute == null) context.getString(string.timeHint) else null
    }
}

@PreviewAnnotation
@Composable
private fun EditorTimeWidgetPreview() {

    val context = LocalContext.current
    val errorText = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TIME).error.toString(context = context)
    val displayableReminderEditorTime = DisplayableTimeResponse(hour = "15", minute = "30")

    MyReminderTheme {
        EditorTimeWidget(
            modifier = Modifier.padding(20.dp),
            displayableResponse = displayableReminderEditorTime,
            isError = true,
            errorText = errorText,
            onClick = {}
        )
    }
}