package com.kotlity.feature_reminder_editor.composables.time

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kotlity.core.ResourcesConstant._1f
import com.kotlity.core.resources.R.*

@Composable
internal fun TimeDialogWidget(
    modifier: Modifier = Modifier,
    dialogProperties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    shape: Shape = MaterialTheme.shapes.large,
    tonalElevation: Dp = dimensionResource(id = dimen._6dp),
    shadowElevation: Dp = dimensionResource(id = dimen._0dp),
    border: BorderStroke? = null,
    @StringRes titleRes: Int = string.selectTimeTitle,
    titleStyle: TextStyle = MaterialTheme.typography.displayMedium,
    @StringRes dismissTextRes: Int = string.cancel,
    @StringRes okTextRes: Int = string.ok,
    textButtonColors: ButtonColors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
    toggle: @Composable () -> Unit
) {
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = dialogProperties
    ) {
        Surface(
            modifier = modifier,
            shape = shape,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
            border = border
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(id = dimen._24dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(id = dimen._20dp))
                    ,
                    text = stringResource(id = titleRes),
                    style = titleStyle
                )
                content()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(_1f))
                    TextButton(
                        onClick = onDismiss,
                        colors = textButtonColors
                    ) {
                        Text(text = stringResource(id = dismissTextRes))
                    }
                    TextButton(
                        onClick = onConfirm,
                        colors = textButtonColors
                    ) {
                        Text(text = stringResource(id = okTextRes))
                    }
                }
            }
        }
    }
}