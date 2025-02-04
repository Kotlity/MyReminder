package com.kotlity.feature_reminder_editor.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotlity.core.composables.TopSectionTitle
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.green
import com.kotlity.core.ui.theme.halfGrey
import com.kotlity.core.ui.theme.red
import com.kotlity.core.ui.theme.white
import com.kotlity.core.util.PreviewAnnotation

@Composable
internal fun TopSection(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceAround,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    isDoneButtonEnabled: Boolean,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        OutlinedIconButton(
            onClick = onBackClick,
            border = IconButtonDefaults.outlinedIconButtonBorder(enabled = true),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = red,
                contentColor = white
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(id = string.backIconButtonDescription)
            )
        }
        TopSectionTitle()
        OutlinedIconButton(
            onClick = onDoneClick,
            enabled = isDoneButtonEnabled,
            border = IconButtonDefaults.outlinedIconButtonBorder(enabled = isDoneButtonEnabled),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = green,
                contentColor = white,
                disabledContainerColor = halfGrey
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(id = string.doneIconButtonDescription)
            )
        }
    }
}

@PreviewAnnotation
@Composable
private fun TopSectionPreview() {

    MyReminderTheme {
        TopSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            isDoneButtonEnabled = true,
            onBackClick = { /*TODO*/ }
        ) {

        }
    }
}