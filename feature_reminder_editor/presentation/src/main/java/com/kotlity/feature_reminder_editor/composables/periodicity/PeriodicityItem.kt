package com.kotlity.feature_reminder_editor.composables.periodicity

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.kotlity.core.resources.R
import com.kotlity.core.ui.theme.halfBlack

@Composable
internal fun PeriodicityItem(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    icon: ImageVector? = null,
    iconTint: Color = halfBlack,
    @StringRes iconContentDescription: Int? = null,
    onClick: () -> Unit
) {

    Row(
        modifier = modifier.clickable { onClick() },
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen._15dp)),
            text = text,
            style = textStyle
        )
        icon?.let { imageVector ->
            Icon(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen._32dp))
                    .padding(end = dimensionResource(id = R.dimen._5dp)),
                imageVector = imageVector,
                tint = iconTint,
                contentDescription = iconContentDescription?.let { stringResource(id = it) }
            )
        }
    }
}