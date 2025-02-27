package com.kotlity.feature_reminders

import android.provider.Settings
import android.text.format.DateFormat
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
import com.kotlity.core.Periodicity
import com.kotlity.core.Reminder
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.ReminderError
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.Event
import com.kotlity.core.util.LocalScreenSize
import com.kotlity.core.util.ObserveAsEvents
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.util.ScreenDimensions
import com.kotlity.core.util.onError
import com.kotlity.core.util.onSuccess
import com.kotlity.core.util.toString
import com.kotlity.feature_reminders.mappers.toIntOffset
import com.kotlity.core.resources.R
import com.kotlity.core.ResourcesConstant
import com.kotlity.core.ResourcesConstant.BLUR_RADIUS_EFFECT
import com.kotlity.core.ResourcesConstant._1_5
import com.kotlity.core.ResourcesConstant._1f
import com.kotlity.feature_reminders.actions.RemindersAction
import com.kotlity.feature_reminders.composables.AddTaskArrowSection
import com.kotlity.feature_reminders.composables.CirclesSection
import com.kotlity.feature_reminders.composables.EmptyRemindersSection
import com.kotlity.feature_reminders.composables.LoadingIndicator
import com.kotlity.feature_reminders.composables.ReminderPopupMenu
import com.kotlity.feature_reminders.composables.Reminders
import com.kotlity.feature_reminders.composables.TopSection
import com.kotlity.feature_reminders.events.ReminderOneTimeEvent
import com.kotlity.feature_reminders.mappers.toReminderUi
import com.kotlity.feature_reminders.states.RemindersState
import com.kotlity.core.util.getActivity
import com.kotlity.feature_reminders.utils.handler
import com.kotlity.core.util.openAppSettings
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
    onEditClick: (Long) -> Unit,
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
        onEditClick = onEditClick,
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
    onEditClick: (Long) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val context = LocalContext.current
    val undoText = stringResource(id = R.string.undo)
    val goToAppSettingsText = stringResource(id = R.string.goToAppSettings)

    var arrowOffset by remember {
        mutableStateOf(IntOffset.Zero)
    }

    val isLoading = remindersState.isLoading
    val areRemindersEmpty = remindersState.reminders.isEmpty()
    val isArrowDisplayed = !isLoading && areRemindersEmpty
    val isPopupMenuDisplayed = remindersState.selectedReminderState.id != null

    ObserveAsEvents(eventFlow) { event ->
        event
            .onError { reminderError ->
                val response = reminderError.toString(context)
                val isAlarmSecurityError = reminderError is ReminderError.Alarm && reminderError.error == AlarmError.SECURITY
                if (isAlarmSecurityError) {
                    val isActionPerformed = onShowSnackbar(response, goToAppSettingsText)
                    if (isActionPerformed) { context.getActivity()?.let { it.openAppSettings(settingsPath = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM) } }
                } else onShowSnackbar(response, null)
            }
            .onSuccess { reminderOneTimeEvent ->
                reminderOneTimeEvent.handler(
                    context,
                    onDelete = { response ->
                        val actionResult = onShowSnackbar(response, undoText)
                        if (actionResult) onReminderAction(RemindersAction.OnReminderRestore)
                    },
                    onEdit = onEditClick
                )
            }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen._10dp)),
            isAddTaskLabelVisible = areRemindersEmpty,
            isAddTaskClickable = !isLoading,
            onAddTaskPositioned = { offset ->
                arrowOffset = offset
            },
            onAddTaskClick = onAddClick
        )

        if (isLoading) {
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

    val context = LocalContext.current

    val is24HourFormat by remember {
        mutableStateOf(DateFormat.is24HourFormat(context))
    }

    val mockReminders = (0..5).map { index ->
        Reminder(
            id = index.toLong(),
            title = "title$index",
            reminderTime = System.currentTimeMillis() + (index + 1).toLong() * 10000,
            periodicity = if (index % 2 == 0) Periodicity.WEEKDAYS else Periodicity.ONCE
        )
    }.map { it.toReminderUi(is24HourFormat = is24HourFormat) }

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
                    onEditClick = { id -> },
                    onShowSnackbar = { _, _ -> true }
                )
            }
        }
    }
}