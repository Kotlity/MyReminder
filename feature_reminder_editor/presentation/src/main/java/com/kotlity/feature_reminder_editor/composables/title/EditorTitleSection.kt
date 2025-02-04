package com.kotlity.feature_reminder_editor.composables.title

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation

@Composable
internal fun EditorTitleSection(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(dimensionResource(id = dimen._2dp)),
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    @StringRes titleRes: Int,
    @DrawableRes iconRes: Int,
    titleStyle: TextStyle = MaterialTheme.typography.displayMedium,
    iconContentDescription: String? = null
) {

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Text(
            text = stringResource(id = titleRes),
            style = titleStyle
        )
        Icon(
            modifier = Modifier.size(dimensionResource(id = dimen._14dp)),
            painter = painterResource(id = iconRes),
            contentDescription = iconContentDescription
        )
    }
}

@PreviewAnnotation
@Composable
private fun EditorTitleSectionPreview() {
    MyReminderTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            EditorTitleSection(
                titleRes = string.title,
                iconRes = drawable.write
            )
        }
    }
}