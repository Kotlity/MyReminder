package com.kotlity.feature_reminder_editor.composables.periodicity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.feature_reminder_editor.composables.title.EditorTitleSection

@Composable
internal fun EditorPeriodicityTitleSection(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int = string.repeat,
    @DrawableRes iconRes: Int = drawable.repeat
) {

    EditorTitleSection(
        modifier = modifier,
        titleRes = titleRes,
        iconRes = iconRes
    )
}

@PreviewAnnotation
@Composable
private fun EditorPeriodicityTitleSectionPreview() {
    MyReminderTheme {
        EditorPeriodicityTitleSection()
    }
}