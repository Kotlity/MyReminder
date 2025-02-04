package com.kotlity.feature_reminder_editor.composables.periodicity

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.halfGreyContainer
import com.kotlity.core.util.PreviewAnnotation

@Composable
internal fun SelectedPeriodicityItem(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    @StringRes iconContentDescription: Int,
    onClick: () -> Unit,
) {

    PeriodicityItem(
        modifier = modifier,
        text = text,
        icon = icon,
        iconContentDescription = iconContentDescription,
        onClick = onClick
    )
}

@PreviewAnnotation
@Composable
private fun SelectedPeriodicityItemPreview() {

    val shape = MaterialTheme.shapes.large
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    val icon = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SelectedPeriodicityItem(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clip(shape)
                    .background(color = halfGreyContainer, shape = shape)
                    .padding(vertical = dimensionResource(id = dimen._5dp)),
                text = "Once",
                icon = icon,
                iconContentDescription = string.periodicityArrowDownDescription,
                onClick = { isExpanded = !isExpanded }
            )
        }
    }
}