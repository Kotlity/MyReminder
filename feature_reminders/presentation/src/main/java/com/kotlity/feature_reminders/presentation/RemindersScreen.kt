package com.kotlity.feature_reminders.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kotlity.core.domain.util.ReminderError
import com.kotlity.core.presentation.util.Event
import com.kotlity.core.presentation.util.ObserveAsEvents
import com.kotlity.core.presentation.util.onError
import com.kotlity.core.presentation.util.onSuccess
import com.kotlity.core.presentation.util.toString
import com.kotlity.feature_reminders.presentation.mappers.toIntOffset
import com.kotlity.core.resources.R
import com.kotlity.core.resources.ResourcesConstant.BLUR_RADIUS_EFFECT
import com.kotlity.core.resources.ResourcesConstant._1_5f
import com.kotlity.feature_reminders.presentation.actions.RemindersAction
import com.kotlity.feature_reminders.presentation.composables.EmptyRemindersSection
import com.kotlity.feature_reminders.presentation.composables.ReminderPopupMenu
import com.kotlity.feature_reminders.presentation.composables.Reminders
import com.kotlity.feature_reminders.presentation.composables.TopSection
import com.kotlity.feature_reminders.presentation.events.ReminderOneTimeEvent
import com.kotlity.feature_reminders.presentation.states.RemindersState
import com.kotlity.feature_reminders.presentation.utils.handler
import com.skydoves.cloudy.cloudy
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel

@Composable
fun RemindersScreen(
    modifier: Modifier = Modifier,
    remindersViewModel: RemindersViewModel = koinViewModel(),
    onAddClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val remindersState by remindersViewModel.state.collectAsStateWithLifecycle()
    val eventFlow = remindersViewModel.eventFlow

    RemindersScreenSection(
        modifier = modifier,
        remindersState = remindersState,
        eventFlow = eventFlow,
        onReminderAction = remindersViewModel::onAction,
        onAddClick = onAddClick,
        onShowSnackbar = onShowSnackbar
    )
}

@Composable
internal fun RemindersScreenSection(
    modifier: Modifier = Modifier,
    remindersState: RemindersState,
    eventFlow: Flow<Event<ReminderOneTimeEvent, ReminderError>>,
    onReminderAction: (RemindersAction) -> Unit,
    onAddClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val context = LocalContext.current
    val undoText = stringResource(id = R.string.undo)

    val areRemindersEmpty = remindersState.reminders.isEmpty()
    val isPopupMenuDisplayed = remindersState.selectedReminderState.id != null

    ObserveAsEvents(eventFlow) { event ->
        event
            .onError { reminderError ->
                val response = reminderError.toString(context)
                onShowSnackbar(response, null)
            }
            .onSuccess { reminderOneTimeEvent ->
                reminderOneTimeEvent.handler(
                    context,
                    onDelete = { response ->
                        val actionResult = onShowSnackbar(response, undoText)
                        if (actionResult) onReminderAction(RemindersAction.OnReminderRestore)
                    },
                    onEdit = { id -> onReminderAction(RemindersAction.OnReminderEdit(id)) }
                )
            }
    }

    Box(modifier = modifier.fillMaxSize()) {
        TopSection(
            modifier = Modifier.fillMaxSize(),
            isAddTaskLabelVisible = areRemindersEmpty,
            onAddTaskClick = onAddClick
        )
        AnimatedContent(
            targetState = areRemindersEmpty,
            label = stringResource(id = R.string.remindersContentSectionLabel)
        ) { areRemindersEmpty ->
            if (areRemindersEmpty) {
                EmptyRemindersSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            } else {
                Reminders(
                    modifier = Modifier
                        .fillMaxSize()
                        .cloudy(
                            radius = BLUR_RADIUS_EFFECT,
                            enabled = isPopupMenuDisplayed
                        ),
                    reminders = remindersState.reminders,
                    userActionsEnabled = !isPopupMenuDisplayed,
                    onReminderClick = { offset, id ->
                        val position = Pair(offset.x.toInt(), offset.y.toInt())
                        onReminderAction(RemindersAction.OnReminderSelect(position, id))
                    }
                )
            }
        }
        if (isPopupMenuDisplayed) {
            Popup(
                offset = remindersState.selectedReminderState.position?.toIntOffset() ?: IntOffset.Zero,
                onDismissRequest = {
                    onReminderAction(RemindersAction.OnReminderUnselect)
                },
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                ReminderPopupMenu(
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen._80dp))
                        .aspectRatio(_1_5f),
                    onEditSectionClick = {
                        remindersState.selectedReminderState.id?.let {
                            onReminderAction(RemindersAction.OnReminderEdit(it))
                        }
                    },
                    onDeleteSectionClick = {
                        remindersState.selectedReminderState.id?.let {
                            onReminderAction(RemindersAction.OnReminderDelete(it))
                        }
                    }
                )
            }
        }
    }
}