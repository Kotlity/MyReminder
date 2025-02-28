package com.kotlity.feature_reminders

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.onSiblings
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.Event
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.UiText
import com.kotlity.feature_reminders.events.ReminderOneTimeEvent
import com.kotlity.feature_reminders.states.RemindersState
import com.kotlity.feature_reminders.states.SelectedReminderState
import com.kotlity.feature_reminders.utils.ActivityRecreationManager
import com.kotlity.feature_reminders.utils.RemindersUtil.firstReminderItem
import com.kotlity.feature_reminders.utils.RemindersUtil.lastReminderItem
import com.kotlity.feature_reminders.utils.RemindersUtil.middleReminderItem
import com.kotlity.feature_reminders.utils.RemindersUtil.mockReminders
import com.kotlity.feature_reminders.utils.remindersActionHandler
import com.kotlity.utils.AndroidComposeTestRuleProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class RemindersScreenTest: AndroidComposeTestRuleProvider<ComponentActivity>(ComponentActivity::class.java) {

    private lateinit var activityRecreationManager: ActivityRecreationManager<ComponentActivity>

    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val title: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.app_name)) }
    private val addIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.addTaskIconDescription)) }
    private val addTask: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.addTask), useUnmergedTree = true) }
    private val loadingIndicator: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.loadingIndicatorTestTag)) }
    private val emptyRemindersLogo: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.emptyRemindersLogoDescription)) }
    private val addYourTaskFirst: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.addYourTaskFirst)) }
    private val circlesSection: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.circlesSectionTestTag)) }
    private val reminders: SemanticsNodeInteraction by lazy { androidComposeTestRule.onNode(hasScrollToNodeAction()) }
    private val reminderPopupMenu: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.reminderPopupMenuTestTag)) }
    private val addTaskArrowSection: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.addTaskArrowSectionTestTag)) }

    private var remindersState by mutableStateOf(RemindersState())
    private val eventFlow = MutableSharedFlow<Event<ReminderOneTimeEvent, ReminderError>>()

    @Before
    fun setup() {
        activityRecreationManager = ActivityRecreationManager(androidComposeTestRule)
        remindersState = RemindersState()
    }
    
    @Test
    fun loading_showsLoadingIndicator_andAddTaskIsNotClickable() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState.copy(isLoading = true),
                    eventFlow = emptyFlow(),
                    onReminderAction = {},
                    onAddClick = {},
                    onEditClick = {},
                    onShowSnackbar = { _, _ -> false }
                )
            }
        }

        title
            .assertIsDisplayed()
            .assertTextEquals(getString(string.app_name))

        loadingIndicator.assertIsDisplayed()

        addIcon
            .assertHasClickAction()
            .assertIsNotEnabled()

        addTask
            .onParent()
            .assertHasClickAction()
            .assertIsNotEnabled()
    }

    @Test
    fun onAddClick_worksCorrectly() {
        var isAddClicked = false

        androidComposeTestRule.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState,
                    eventFlow = emptyFlow(),
                    onReminderAction = {},
                    onAddClick = { isAddClicked = true },
                    onEditClick = {},
                    onShowSnackbar = { _, _ -> false }
                )
            }
        }

        addIcon
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        assertThat(isAddClicked).isTrue()
    }
    
    @Test
    fun whenRemindersAreEmpty_showsEmptyRemindersSection() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState,
                    eventFlow = emptyFlow(),
                    onReminderAction = {},
                    onAddClick = {},
                    onEditClick = {} ,
                    onShowSnackbar = { _, _ -> false }
                )
            }
        }

        reminders.assertDoesNotExist()

        addIcon.assertIsEnabled()

        addTask.assertIsDisplayed()

        emptyRemindersLogo.assertIsDisplayed()

        addYourTaskFirst.assertIsDisplayed()

        circlesSection.assertIsDisplayed()

        addTaskArrowSection.assertIsDisplayed()
    }

    @Test
    fun whenHasReminders_scrollToTheCenterOfTheList_clickOnReminderExpandIcon_showsReminderPopupMenu() {
        var selectedReminderId: Long? = null
        val middleReminderItem = middleReminderItem

        androidComposeTestRule.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState.copy(reminders = mockReminders),
                    eventFlow = emptyFlow(),
                    onReminderAction = { remindersAction ->
                        remindersAction.remindersActionHandler(
                            onReminderSelect = { position, id ->
                                remindersState = remindersState.copy(
                                    selectedReminderState = SelectedReminderState(id = id, position = position)
                                )
                                selectedReminderId = id
                            }
                        )
                    },
                    onAddClick = {},
                    onEditClick = {},
                    onShowSnackbar = { _, _ -> false }
                )
            }
        }

        reminders
            .assertIsDisplayed()
            .performScrollToNode(hasText(middleReminderItem.title))

        onNodeWithText(middleReminderItem.title)
            .assertIsDisplayed()
            .onSiblings()
            .filterToOne(hasContentDescription(getString(string.expandIconDescription)))
            .assertIsDisplayed()
            .performClick()

        onNodeWithText(getString(string.edit))
            .assertIsDisplayed()
            .assertHasClickAction()

        onNodeWithText(getString(string.delete))
            .assertIsDisplayed()
            .assertHasClickAction()

        assertThat(selectedReminderId).isEqualTo(middleReminderItem.id)
    }

    @Test
    fun onBackPress_hideReminderPopupMenu() {
        remindersState = remindersState.copy(
            reminders = mockReminders,
            selectedReminderState = SelectedReminderState(id = middleReminderItem.id)
        )
        androidComposeTestRule.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState,
                    eventFlow = emptyFlow(),
                    onReminderAction = { remindersAction ->
                        remindersAction.remindersActionHandler(
                            onReminderUnselect = {
                                remindersState = remindersState.copy(selectedReminderState = SelectedReminderState())
                            }
                        )
                    },
                    onAddClick = {},
                    onEditClick = {},
                    onShowSnackbar = { _, _ -> false }
                )
            }
        }

        reminderPopupMenu.assertIsDisplayed()

        assertThat(remindersState.selectedReminderState.id).isEqualTo(middleReminderItem.id)

        uiDevice.pressBack()

        reminderPopupMenu.assertDoesNotExist()

        assertThat(remindersState.selectedReminderState.id).isNull()
    }

    @Test
    fun whenReminderPopupMenuIsDisplayed_rotatesScreen_savedReminderPopupMenuVisibility() {
        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_PORTRAIT)

        activityRecreationManager.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState.copy(
                        reminders = mockReminders,
                        selectedReminderState = SelectedReminderState(id = lastReminderItem.id)
                    ),
                    eventFlow = emptyFlow(),
                    onReminderAction = {},
                    onAddClick = {},
                    onEditClick = {},
                    onShowSnackbar = { _, _ -> false }
                )
            }
        }

        reminderPopupMenu.assertIsDisplayed()

        activityRecreationManager.recreateWith { changeScreenOrientation(ScreenOrientation.LANDSCAPE) }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        reminderPopupMenu.assertIsDisplayed()
    }

    @Test
    fun onEditClick_hideReminderPopupMenuAndSavesSelectedReminderId() {
        var selectedReminderId: Long? = null
        remindersState = remindersState.copy(
            reminders = mockReminders,
            selectedReminderState = SelectedReminderState(id = firstReminderItem.id)
        )
        val eventToEmit = Event.Success(data = ReminderOneTimeEvent.Edit(id = firstReminderItem.id))

        androidComposeTestRule.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState,
                    eventFlow = eventFlow.asSharedFlow(),
                    onReminderAction = { remindersAction ->
                        remindersAction.remindersActionHandler(
                            onReminderEdit = { id ->
                                remindersState = remindersState.copy(
                                    selectedReminderState = SelectedReminderState()
                                )
                                runBlocking { eventFlow.emit(eventToEmit) }
                            }
                        )
                    },
                    onAddClick = {},
                    onEditClick = { id ->
                        selectedReminderId = id
                    },
                    onShowSnackbar = { _, _ -> false }
                )
            }
        }

        onNodeWithText(getString(string.edit)).performClick()

        reminderPopupMenu.assertDoesNotExist()

        assertThat(remindersState.selectedReminderState.id).isNull()

        assertThat(selectedReminderId).isEqualTo(firstReminderItem.id)
    }

    @Test
    fun onDeleteClick_hideReminderPopupMenu_deletesReminderAndShowsSnackbar() {
        var snackbarMessage: String? = null
        val updatedReminders = mockReminders.toMutableList()
        remindersState = remindersState.copy(
            reminders = updatedReminders,
            selectedReminderState = SelectedReminderState(id = middleReminderItem.id)
        )
        val eventToEmit = Event.Success(ReminderOneTimeEvent.Delete(result = UiText.StringResource(string.reminderSuccessfullyDeleted)))
        val expectedSnackbarMessage = eventToEmit.data.result.asString(context)

        androidComposeTestRule.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState,
                    eventFlow = eventFlow.asSharedFlow(),
                    onReminderAction = { remindersAction ->
                        remindersAction.remindersActionHandler(
                            onReminderDelete = { id ->
                                val deletedReminder = updatedReminders.find { it.id == id }!!
                                updatedReminders.remove(deletedReminder)
                                remindersState = remindersState.copy(
                                    reminders = updatedReminders,
                                    selectedReminderState = SelectedReminderState()
                                )
                                runBlocking { eventFlow.emit(eventToEmit) }
                            }
                        )
                    },
                    onAddClick = {},
                    onEditClick = {},
                    onShowSnackbar = { message, _ ->
                        snackbarMessage = message
                        false
                    }
                )
            }
        }

        onNodeWithText(getString(string.delete)).performClick()

        reminderPopupMenu.assertDoesNotExist()

        assertThat(remindersState.selectedReminderState.id).isNull()

        assertThat(snackbarMessage).isEqualTo(expectedSnackbarMessage)

        reminders.performScrollToNode(hasText(lastReminderItem.title))

        onNodeWithText(middleReminderItem.title).assertDoesNotExist()
    }

    @Test
    fun onSnackbarUndoClick_restoreRecentlyDeletedReminder() {
        val updatedReminders = mockReminders.toMutableList()
        val executionReminder = mockReminders[mockReminders.size / 3]
        val executionReminderIndex = mockReminders.indexOf(executionReminder)
        remindersState = remindersState.copy(reminders = updatedReminders)

        var undoText: String? = null
        val deleteEvent = Event.Success(data = ReminderOneTimeEvent.Delete(result = UiText.StringResource(string.reminderSuccessfullyDeleted)))

        androidComposeTestRule.setContent {
            MyReminderTheme {
                RemindersScreenSection(
                    remindersState = remindersState,
                    eventFlow = eventFlow.asSharedFlow(),
                    onReminderAction = { remindersAction ->
                        remindersAction.remindersActionHandler(
                            onReminderSelect = { position, id ->
                                remindersState = remindersState.copy(
                                    selectedReminderState = SelectedReminderState(id = id, position = position)
                                )
                            },
                            onReminderDelete = { id ->
                                val deletedReminder = updatedReminders.find { it.id == id }!!
                                updatedReminders.remove(deletedReminder)
                                remindersState = remindersState.copy(
                                    reminders = updatedReminders,
                                    selectedReminderState = SelectedReminderState()
                                )
                                runBlocking { eventFlow.emit(deleteEvent) }
                            },
                            onReminderRestore = {
                                updatedReminders.add(index = executionReminderIndex, element = executionReminder)
                                remindersState = remindersState.copy(reminders = updatedReminders)
                            },
                            onReminderUnselect = {
                                remindersState = remindersState.copy(
                                    selectedReminderState = SelectedReminderState()
                                )
                            }
                        )
                    },
                    onAddClick = {},
                    onEditClick = {},
                    onShowSnackbar = { _, action ->
                        undoText = action
                        true
                    }
                )
            }
        }

        reminders.performScrollToNode(hasText(executionReminder.title))

        onNodeWithText(executionReminder.title)
            .onSiblings()
            .filterToOne(hasContentDescription(getString(string.expandIconDescription)))
            .performClick()

        onNodeWithText(getString(string.delete)).performClick()

        assertThat(undoText).isEqualTo(getString(string.undo))

        onNodeWithText(executionReminder.title).assertIsDisplayed()
    }

}