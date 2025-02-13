package com.kotlity.feature_reminder_editor.composables.time

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.feature_reminder_editor.composables.EditorTitleSection
import com.kotlity.feature_reminder_editor.models.HourFormat

@Composable
internal fun EditorTimeTitleSection(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(dimensionResource(id = dimen._2dp)),
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    @StringRes titleRes: Int = string.time,
    @DrawableRes iconRes: Int = drawable.time,
    @StringRes iconContentDescription: Int = string.timeIconDescription,
    hourFormat: HourFormat?,
    hourFormatStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        EditorTitleSection(
            titleRes = titleRes,
            iconRes = iconRes,
            iconContentDescription = stringResource(id = iconContentDescription)
        )
        hourFormat?.let { format ->
            Text(
                modifier = Modifier.testTag(stringResource(id = string.timeTextHourFormatTestTag)),
                text = format.name,
                style = hourFormatStyle
            )
        }
    }
}

@PreviewAnnotation
@Composable
private fun EditorTimeTitleSectionPreview() {
    MyReminderTheme {
        EditorTimeTitleSection(hourFormat = HourFormat.AM)
    }
}