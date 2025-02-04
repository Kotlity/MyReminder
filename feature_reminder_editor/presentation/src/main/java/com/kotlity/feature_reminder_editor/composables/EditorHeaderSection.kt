package com.kotlity.feature_reminder_editor.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.kotlity.core.ResourcesConstant._16sp
import com.kotlity.core.ResourcesConstant._18sp
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.white
import com.kotlity.core.util.PreviewAnnotation

@Composable
internal fun EditorHeaderSection(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = white
    ),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    @StringRes titleRes: Int = string.makeYourOwnReminder,
    titleStyle: TextStyle = MaterialTheme.typography.displayLarge.copy(fontSize = _18sp)
) {

    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        Text(
            modifier = Modifier.padding(
                vertical = dimensionResource(id = dimen._7dp),
                horizontal = dimensionResource(id = dimen._20dp)
            ),
            text = stringResource(id = titleRes),
            style = titleStyle
        )
    }
}

@PreviewAnnotation
@Composable
private fun EditorHeaderSectionPreview() {

    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EditorHeaderSection()
        }
    }
}