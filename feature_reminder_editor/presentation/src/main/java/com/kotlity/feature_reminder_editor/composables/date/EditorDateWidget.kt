package com.kotlity.feature_reminder_editor.composables.date

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kotlity.core.ResourcesConstant
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.red
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.toString
import com.kotlity.feature_reminder_editor.composables.EditorTextField
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.utils.TextFieldHintProvider

@Composable
internal fun EditorDateWidget(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = dimen._4dp)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    displayableReminderEditorDate: DisplayableReminderEditorDate,
    isError: Boolean,
    errorText: String?,
    errorTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = red),
    @StringRes dividerRes: Int = string.timeDateDivider,
    onClick: () -> Unit
) {

    val context = LocalContext.current

    val textFieldsModifier = Modifier.width(IntrinsicSize.Min)
    val horizontalPadding = PaddingValues(horizontal = dimensionResource(id = dimen._5dp))

    val dayHint = DayTextFieldHintProvider(context = context).getHint(data = displayableReminderEditorDate.day)
    val monthHint = MonthTextFieldHintProvider(context = context).getHint(data = displayableReminderEditorDate.month)
    val yearHint = YearTextFieldHintProvider(context = context).getHint(data = displayableReminderEditorDate.year)

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
                text = displayableReminderEditorDate.day ?: "",
                onTextChange = {},
                enabled = false,
                isError = isError,
                hint = dayHint,
                testTagRes = string.dayTextFieldTestTag,
                hintPadding = horizontalPadding,
                onClick = onClick
            )
            Text(text = stringResource(id = dividerRes))
            EditorTextField(
                modifier = textFieldsModifier,
                text = displayableReminderEditorDate.month ?: "",
                onTextChange = {},
                enabled = false,
                isError = isError,
                hint = monthHint,
                testTagRes = string.monthTextFieldTestTag,
                hintPadding = horizontalPadding,
                onClick = onClick
            )
            Text(text = stringResource(id = dividerRes))
            EditorTextField(
                modifier = textFieldsModifier,
                text = displayableReminderEditorDate.year ?: "",
                onTextChange = {},
                enabled = false,
                isError = isError,
                hint = yearHint,
                testTagRes = string.yearTextFieldTestTag,
                hintPadding = horizontalPadding,
                onClick = onClick
            )
        }
        if (isError) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(ResourcesConstant._0_5)
                    .testTag(stringResource(id = string.dateTextFieldErrorTestTag)),
                text = errorText!!,
                style = errorTextStyle
            )
        }
    }
}

private class DayTextFieldHintProvider(private val context: Context): TextFieldHintProvider<String?> {

    override fun getHint(data: String?): String? {
        return if (data == null) context.getString(string.dayHint) else null
    }
}

private class MonthTextFieldHintProvider(private val context: Context): TextFieldHintProvider<String?> {

    override fun getHint(data: String?): String? {
        return if (data == null) context.getString(string.monthHint) else null
    }
}

private class YearTextFieldHintProvider(private val context: Context): TextFieldHintProvider<String?> {

    override fun getHint(data: String?): String? {
        return if (data == null) context.getString(string.yearHint) else null
    }
}

@PreviewAnnotation
@Composable
private fun EditorDateWidgetPreview() {

    val context = LocalContext.current
    val errorText = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TIME).error.toString(context = context)
    val displayableReminderEditorDate = DisplayableReminderEditorDate()

    MyReminderTheme {
        EditorDateWidget(
            modifier = Modifier.padding(20.dp),
            displayableReminderEditorDate = displayableReminderEditorDate,
            isError = true,
            errorText = errorText,
            onClick = {}
        )
    }
}