package com.kotlity.feature_reminders.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kotlity.core.Periodicity
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.linearGradientColor1
import com.kotlity.core.ui.theme.linearGradientColor2
import com.kotlity.core.ui.theme.white
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.resources.R.*
import com.kotlity.core.util.mapToString
import com.kotlity.feature_reminders.models.DisplayableReminderTime
import com.kotlity.feature_reminders.models.ReminderUi

@Composable
fun ReminderItem(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    colors: CardColors = CardDefaults.cardColors(containerColor = white),
    border: BorderStroke = BorderStroke(
        width = dimensionResource(id = dimen._1dp),
        brush = Brush.linearGradient(colors = listOf(linearGradientColor1, linearGradientColor2))
    ),
    reminderUi: ReminderUi,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    timeStyle: TextStyle = MaterialTheme.typography.titleLarge,
    dateStyle: TextStyle = MaterialTheme.typography.bodySmall,
    @DrawableRes expandIconRes: Int = drawable.expand,
    @StringRes expandIconContentDescription: Int = string.expandIconDescription,
    @StringRes reminderTimeTextTestTag: Int = string.reminderTimeTextTestTag,
    @StringRes reminderDateTextTestTag: Int = string.reminderDateTextTestTag,
    @StringRes reminderPeriodicityTextTestTag: Int = string.reminderPeriodicityTextTestTag,
    onReminderExpandIconClick: (Offset) -> Unit
) {

    val context = LocalContext.current

    var position by remember {
        mutableStateOf(Offset.Zero)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = dimensionResource(id = dimen._10dp),
                shape = MaterialTheme.shapes.medium,
                spotColor = linearGradientColor1
            ),
        shape = shape,
        colors = colors,
        border = border
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = dimen._20dp),
                    top = dimensionResource(id = dimen._20dp),
                    end = dimensionResource(id = dimen._20dp),
                    bottom = dimensionResource(id = dimen._5dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(

            ) {
                Text(
                    text = reminderUi.title,
                    style = titleStyle
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = dimen._3dp)))
                Text(
                    modifier = Modifier.testTag(stringResource(id = reminderDateTextTestTag)),
                    text = reminderUi.reminderTime.date,
                    style = dateStyle
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = dimen._10dp)))
                Image(
                    modifier = Modifier
                        .onGloballyPositioned {
                            position = it.positionInRoot()
                        }
                        .size(dimensionResource(id = dimen._36dp))
                        .clickable {
                            onReminderExpandIconClick(position)
                        },
                    painter = painterResource(id = expandIconRes),
                    contentDescription = stringResource(id = expandIconContentDescription)
                )
            }
            Column {
                Text(
                    modifier = Modifier.testTag(stringResource(id = reminderTimeTextTestTag)),
                    text = reminderUi.reminderTime.time,
                    style = timeStyle
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = dimen._3dp)))
                Text(
                    modifier = Modifier.testTag(stringResource(id = reminderPeriodicityTextTestTag)),
                    text = reminderUi.periodicity.mapToString(context),
                    style = dateStyle
                )
            }
        }
    }
}

@PreviewAnnotation
@Composable
private fun ReminderItemPreview() {
    MyReminderTheme {
        ReminderItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 30.dp)
                .shadow(10.dp, MaterialTheme.shapes.small, spotColor = linearGradientColor1),
            reminderUi = ReminderUi(
                id = 10,
                title = "Tugas PPL",
                reminderTime = DisplayableReminderTime(value = 21312, time = "05:00", date = "19/06/2023"),
                periodicity = Periodicity.ONCE
            )
        ) {

        }
    }
}