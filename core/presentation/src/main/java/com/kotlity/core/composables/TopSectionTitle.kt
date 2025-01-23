package com.kotlity.core.composables

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.kotlity.core.resources.R.*

@Composable
fun TopSectionTitle(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int = string.app_name,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge
) {
    Text(
        modifier = modifier,
        text = stringResource(id = titleRes),
        style = titleStyle
    )
}