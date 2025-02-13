package com.kotlity.feature_reminder_editor.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.kotlity.core.ResourcesConstant
import com.kotlity.core.resources.R
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.black
import com.kotlity.core.ui.theme.red
import com.kotlity.core.util.PreviewAnnotation

@Composable
internal fun EditorTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    hintStyle: TextStyle = MaterialTheme.typography.displaySmall,
    textStyle: TextStyle = hintStyle.copy(fontSize = ResourcesConstant._18sp),
    isError: Boolean,
    hint: String?,
    @StringRes testTagRes: Int,
    hintPadding: PaddingValues? = null,
    onFocusChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {

    val conditionColor = if (isError) red else black
    val additionalModifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier

    Column(
        modifier = modifier
    ) {
        BasicTextField(
            modifier = Modifier
                .then(if (onFocusChange != null) Modifier.onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                } else Modifier)
                .testTag(stringResource(id = testTagRes)),
            value = text,
            onValueChange = onTextChange,
            enabled = enabled,
            singleLine = singleLine,
            textStyle = textStyle.copy(color = conditionColor),
            cursorBrush = SolidColor(value = conditionColor),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen._4dp))
                        .then(additionalModifier),
                    contentAlignment = Alignment.BottomStart
                ) {
                    hint?.let {
                        Text(
                            modifier = hintPadding?.let { padding -> Modifier.padding(paddingValues = padding) } ?: Modifier,
                            text = it,
                            style = hintStyle
                        )
                    }
                    innerTextField()
                }
            }
        )
        HorizontalDivider(
            thickness = dimensionResource(id = R.dimen._1dp),
            color = if (isError) red else Color.Gray
        )
    }
}

@PreviewAnnotation
@Composable
private fun EditorTextFieldPreview() {

    var text by remember {
        mutableStateOf("")
    }

    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EditorTextField(
                text = text,
                onTextChange = { text = it },
                isError = true,
                hint = null,
                testTagRes = R.string.titleTextFieldTestTag
            )
        }
    }
}