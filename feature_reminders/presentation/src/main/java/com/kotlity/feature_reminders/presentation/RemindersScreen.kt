package com.kotlity.feature_reminders.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kotlity.core.domain.Periodicity
import com.kotlity.core.domain.Reminder
import com.kotlity.core.domain.util.ReminderError
import com.kotlity.core.presentation.ui.theme.MyReminderTheme
import com.kotlity.core.presentation.util.Event
import com.kotlity.core.presentation.util.LocalScreenSize
import com.kotlity.core.presentation.util.ObserveAsEvents
import com.kotlity.core.presentation.util.PreviewAnnotation
import com.kotlity.core.presentation.util.ScreenDimensions
import com.kotlity.core.presentation.util.onError
import com.kotlity.core.presentation.util.onSuccess
import com.kotlity.core.presentation.util.toString
import com.kotlity.feature_reminders.presentation.mappers.toIntOffset
import com.kotlity.core.resources.R
import com.kotlity.core.resources.ResourcesConstant
import com.kotlity.core.resources.ResourcesConstant.BLUR_RADIUS_EFFECT
import com.kotlity.core.resources.ResourcesConstant._1_5
import com.kotlity.core.resources.ResourcesConstant._1f
import com.kotlity.feature_reminders.presentation.actions.RemindersAction
import com.kotlity.feature_reminders.presentation.composables.AddTaskArrowSection
import com.kotlity.feature_reminders.presentation.composables.CirclesSection
import com.kotlity.feature_reminders.presentation.composables.EmptyRemindersSection
import com.kotlity.feature_reminders.presentation.composables.LoadingIndicator
import com.kotlity.feature_reminders.presentation.composables.ReminderPopupMenu
import com.kotlity.feature_reminders.presentation.composables.Reminders
import com.kotlity.feature_reminders.presentation.composables.TopSection
import com.kotlity.feature_reminders.presentation.events.ReminderOneTimeEvent
import com.kotlity.feature_reminders.presentation.mappers.toReminderUi
import com.kotlity.feature_reminders.presentation.states.RemindersState
import com.kotlity.feature_reminders.presentation.utils.handler
import com.skydoves.cloudy.cloudy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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

    var arrowOffset by remember {
        mutableStateOf(IntOffset.Zero)
    }

    val areRemindersEmpty = remindersState.reminders.isEmpty()
    val isArrowDisplayed = !remindersState.isLoading && areRemindersEmpty
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

    Column(modifier = modifier.fillMaxSize()) {
        TopSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen._10dp)),
            isAddTaskLabelVisible = areRemindersEmpty,
            onAddTaskPositioned = { offset ->
                arrowOffset = offset
            },
            onAddTaskClick = onAddClick
        )

        if (remindersState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(modifier = Modifier.size(dimensionResource(id = R.dimen._86dp)))
            }
        } else {
            AnimatedContent(
                targetState = areRemindersEmpty,
                label = stringResource(id = R.string.remindersContentSectionLabel)
            ) { areRemindersEmpty ->
                if (areRemindersEmpty) {
                    EmptyRemindersSection(modifier = Modifier.fillMaxSize())
                    CirclesSection(modifier = Modifier.fillMaxSize())
                } else {
                    Reminders(
                        modifier = Modifier
                            .weight(_1f)
                            .fillMaxWidth()
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
                        .aspectRatio(_1_5),
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
    if (isArrowDisplayed) {
        AddTaskArrowSection(
            modifier = Modifier
                .width(dimensionResource(id = R.dimen._100dp))
                .aspectRatio(ResourcesConstant._0_5)
                .offset { arrowOffset }
        )
    }
}


@PreviewAnnotation
@Composable
fun RemindersScreenSectionPreview() {
    val localConfiguration = LocalConfiguration.current
    val screenWidth = localConfiguration.screenWidthDp.dp
    val screenHeight = localConfiguration.screenHeightDp.dp

    val screenDimensions = remember(localConfiguration) {
        ScreenDimensions(width = screenWidth, height = screenHeight)
    }

    var remindersState by remember {
        mutableStateOf(RemindersState())
    }

    val mockReminders = (0..5).map { index ->
        Reminder(
            id = index.toLong(),
            title = "title$index",
            reminderTime = 1734127200000 + index.toLong() * 1000,
            periodicity = if (index % 2 == 0) Periodicity.WEEKDAYS else Periodicity.ONCE
        )
    }.map { it.toReminderUi() }

    LaunchedEffect(key1 = Unit) {
        remindersState = remindersState.copy(isLoading = true)
        delay(3000)
        remindersState = remindersState.copy(
            isLoading = false,
            reminders = mockReminders
        )
    }

    CompositionLocalProvider(LocalScreenSize provides screenDimensions) {
        MyReminderTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                RemindersScreenSection(
                    modifier = Modifier.padding(innerPadding),
                    remindersState = remindersState,
                    eventFlow = emptyFlow(),
                    onReminderAction = { },
                    onAddClick = { /*TODO*/ },
                    onShowSnackbar = { _, _ -> true }
                )
            }
        }
    }
}