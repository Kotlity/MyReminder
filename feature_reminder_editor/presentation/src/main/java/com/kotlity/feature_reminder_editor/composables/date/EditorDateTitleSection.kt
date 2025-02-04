package com.kotlity.feature_reminder_editor.composables.date

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.feature_reminder_editor.composables.title.EditorTitleSection

@Composable
fun EditorDateTitleSection(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int = string.calendar,
    @DrawableRes iconRes: Int = drawable.calendar,
    @StringRes iconContentDescription: Int = string.dateIconDescription,
) {

    EditorTitleSection(
        modifier = modifier,
        titleRes = titleRes,
        iconRes = iconRes,
        iconContentDescription = stringResource(id = iconContentDescription)
    )
}

@PreviewAnnotation
@Composable
private fun EditorDateTitleSectionPreview() {
    MyReminderTheme {
        EditorDateTitleSection()
    }
}