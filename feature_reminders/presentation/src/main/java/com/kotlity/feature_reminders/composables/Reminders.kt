package com.kotlity.feature_reminders.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kotlity.core.Periodicity
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.resources.R.*
import com.kotlity.core.ResourcesConstant._1_5
import com.kotlity.feature_reminders.models.DisplayableReminderTime
import com.kotlity.feature_reminders.models.ReminderUi
import com.skydoves.cloudy.cloudy
import kotlin.random.Random

@Composable
internal fun Reminders(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(dimensionResource(id = dimen._10dp)),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensionResource(id = dimen._10dp)),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    userActionsEnabled: Boolean = true,
    reminders: List<ReminderUi>,
    onReminderClick: (Offset, Long) -> Unit
) {

    val density = LocalDensity.current

    val updatedOffsetX = with(density) { dimensionResource(id = dimen.minus15dp).toPx() }
    val updatedOffsetY = with(density) { dimensionResource(id = dimen._15dp).toPx() }

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        userScrollEnabled = userActionsEnabled
    ) {
        items(
            items = reminders,
            key = { item -> item.id }
        ) { reminder ->
            ReminderItem(
                modifier = Modifier.animateItem(),
                reminderUi = reminder,
                onReminderExpandIconClick = { position ->
                    if (!userActionsEnabled) return@ReminderItem
                    val updatedPosition = Offset(x = position.x + updatedOffsetX, y = position.y + updatedOffsetY)
                    onReminderClick(updatedPosition, reminder.id)
                }
            )
        }
    }
}

@PreviewAnnotation
@Composable
private fun ReminderItemListWithPopupPreview() {

    var reminders by remember {
        mutableStateOf((0..10).map {
            ReminderUi(
                id = it.toLong(),
                title = "Reminder #$it",
                reminderTime = DisplayableReminderTime(value = Random.nextLong(from = 12312312312, until = 19894538911), time = "$it:00",date = "$it/06/2023"),
                periodicity = Periodicity.ONCE
            )
        })
    }

    var isReminderPopupMenuDisplayed by rememberSaveable {
        mutableStateOf(false)
    }

    var reminderPopupMenuOffset by remember {
        mutableStateOf(IntOffset.Zero)
    }

    var selectedReminderId by rememberSaveable {
        mutableLongStateOf(-1)
    }

    MyReminderTheme {
        Reminders(
            modifier = Modifier
                .fillMaxSize()
                .cloudy(
                    radius = 14,
                    enabled = isReminderPopupMenuDisplayed
                ),
            userActionsEnabled = !isReminderPopupMenuDisplayed,
            reminders = reminders,
            onReminderClick = { position, id ->
                isReminderPopupMenuDisplayed = true
                reminderPopupMenuOffset = IntOffset(x = position.x.toInt(), y = position.y.toInt())
                selectedReminderId = id
            }
        )
        if (isReminderPopupMenuDisplayed) {
            Popup(
                offset = reminderPopupMenuOffset,
                onDismissRequest = {
                    isReminderPopupMenuDisplayed = false
                    selectedReminderId = -1
                },
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                ReminderPopupMenu(
                    modifier = Modifier
                        .height(dimensionResource(id = dimen._80dp))
                        .aspectRatio(_1_5),
                    onEditSectionClick = {
                        isReminderPopupMenuDisplayed = false
                        selectedReminderId = -1
                    },
                    onDeleteSectionClick = {
                        isReminderPopupMenuDisplayed = false
                        if (selectedReminderId != (-1).toLong()) {
                            val updatedReminders = reminders.toMutableList()
                            updatedReminders.removeIf { it.id == selectedReminderId }
                            reminders = updatedReminders
                        }
                        selectedReminderId = -1
                    }
                )
            }
        }
    }
}