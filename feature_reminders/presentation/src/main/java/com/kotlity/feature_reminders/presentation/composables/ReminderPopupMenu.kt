package com.kotlity.feature_reminders.presentation.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kotlity.core.presentation.ui.theme.MyReminderTheme
import com.kotlity.core.presentation.ui.theme.darkBlack
import com.kotlity.core.presentation.ui.theme.red
import com.kotlity.core.presentation.ui.theme.white
import com.kotlity.core.presentation.util.PreviewAnnotation
import com.kotlity.core.resources.R
import com.kotlity.core.resources.ResourcesConstant._18sp
import com.kotlity.core.resources.ResourcesConstant._1f

@Composable
fun ReminderPopupMenu(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MaterialTheme.shapes.extraSmall,
    colors: CardColors = CardDefaults.cardColors(containerColor = white),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen._10dp)),
    border: BorderStroke = BorderStroke(
        width = dimensionResource(id = R.dimen._2dp),
        color = darkBlack
    ),
    onEditSectionClick: () -> Unit,
    onDeleteSectionClick: () -> Unit
) {

    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        Column {
            ReminderDropdownMenuTextButton(
                shape = shape,
                textRes = R.string.edit,
                onClick = onEditSectionClick
            )
            HorizontalDivider(color = darkBlack)
            ReminderDropdownMenuTextButton(
                shape = shape,
                textRes = R.string.delete,
                color = red,
                onClick = onDeleteSectionClick
            )
        }
    }
}

@Composable
private fun ColumnScope.ReminderDropdownMenuTextButton(
    shape: CornerBasedShape,
    @StringRes textRes: Int,
    color: Color = darkBlack,
    textSize: TextUnit = _18sp,
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .weight(_1f),
        onClick = onClick,
        shape = shape
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = textRes),
                style = TextStyle(
                    color = color,
                    fontSize = textSize
                )
            )
        }
    }
}

@PreviewAnnotation
@Composable
private fun ReminderDropdownMenuPreview() {
    MyReminderTheme {
        ReminderPopupMenu(
            modifier = Modifier
                .height(100.dp)
                .aspectRatio(1.5f)
                .padding(start = 20.dp, top = 20.dp),
            onEditSectionClick = { /*TODO*/ },
            onDeleteSectionClick = { /*TODO*/ }
        )
    }
}