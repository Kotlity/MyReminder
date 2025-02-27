package com.kotlity.feature_reminder_editor

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.Periodicity
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.Event
import com.kotlity.core.util.UiText
import com.kotlity.core.util.ValidationStatus
import com.kotlity.feature_reminder_editor.events.ReminderEditorOneTimeEvent
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTimeHourFormat
import com.kotlity.feature_reminder_editor.models.DisplayableTimeResponse
import com.kotlity.feature_reminder_editor.models.HourFormat
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.models.ReminderEditorUi
import com.kotlity.feature_reminder_editor.models.ValidationStatuses
import com.kotlity.feature_reminder_editor.states.ReminderEditorState
import com.kotlity.feature_reminder_editor.utils.DateUtil
import com.kotlity.feature_reminder_editor.utils.TimeUtil
import com.kotlity.feature_reminder_editor.utils.reminderEditorActionHandler
import com.kotlity.utils.AndroidComposeTestRuleProvider
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class ReminderEditorScreenTest: AndroidComposeTestRuleProvider<ComponentActivity>(ComponentActivity::class.java) {

    private val timeUtil: TimeUtil<ComponentActivity> = TimeUtil(androidComposeTestRule)
    private val dateUtil: DateUtil<ComponentActivity> = DateUtil(androidComposeTestRule)

    private val backIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.backIconButtonDescription)) }
    private val doneIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.doneIconButtonDescription)) }
    private val titleTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.titleTextFieldTestTag)) }
    private val titleTextFieldHint: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.insertTitle)) }
    private val timeTextHourFormat: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.timeTextHourFormatTestTag)) }
    private val hourTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.hourTextFieldTestTag)) }
    private val minuteTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.minuteTextFieldTestTag)) }
    private val hourAndMinuteTextFieldHints: SemanticsNodeInteractionCollection by lazy { androidComposeTestRule.onAllNodesWithText(getString(string.timeHourAndMinuteHint)) }
    private val timeTextFieldError: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.timeTextFieldErrorTestTag)) }
    private val dayTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.dayTextFieldTestTag)) }
    private val monthTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.monthTextFieldTestTag)) }
    private val yearTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.yearTextFieldTestTag)) }
    private val dateTextFieldHints: List<SemanticsNodeInteraction> by lazy { listOf(
        onNodeWithText(getString(string.dayHint)),
        onNodeWithText(getString(string.monthHint)),
        onNodeWithText(getString(string.yearHint))
    ) }
    private val selectedPeriodicityItem: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.selectedPeriodicityItemTestTag), useUnmergedTree = true) }
    private val selectedPeriodicityItemTextNode: SemanticsNodeInteraction by lazy { selectedPeriodicityItem.onChildren().onFirst() }
    private val periodicityMenu: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.periodicityListTestTag)) }

    private val dateTextFieldError: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.dateTextFieldErrorTestTag)) }

    private val hourAndMinuteRadioButtons: SemanticsNodeInteractionCollection by lazy {
        val hasRadioButtonRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
        androidComposeTestRule.onAllNodes(hasRadioButtonRole)
    }
    private val hourRadioButton: SemanticsNodeInteraction by lazy {
        hourAndMinuteRadioButtons.onFirst()
    }
    private val minuteRadioButton: SemanticsNodeInteraction by lazy {
        hourAndMinuteRadioButtons.onLast()
    }

    private val okTextButton: SemanticsNodeInteraction by lazy { onNodeWithText(okText) }
    private val okText = getString(string.ok)

    private val once = getString(string.once)

    private val titleTextFieldHintText = getString(string.insertTitle)
    private val timeTextFieldErrorText = getString(string.reminderTimePastTense)
    private val dateTextFieldErrorText = getString(string.reminderDateWrongPeriodicity)

    private var reminderEditorState by mutableStateOf(ReminderEditorState())
    private var validationStatuses by mutableStateOf(ValidationStatuses())

    private fun onPickerDialogUpdate(pickerDialog: PickerDialog?) {
        reminderEditorState = reminderEditorState.copy(pickerDialog = pickerDialog)
    }

    private fun onTimeUpdate(timeResponse: Pair<Int, Int>) {
        val updatedTime = reminderEditorState.reminderEditor.copy(reminderEditorTime = timeResponse.toDisplayableReminderEditorTime(reminderEditorState.reminderEditor.is24HourFormat))
        reminderEditorState = reminderEditorState.copy(reminderEditor = updatedTime)
    }

    private fun onDateUpdate(choosenDateInMillis: Long) {
        val updatedDate = reminderEditorState.reminderEditor.copy(reminderEditorDate = choosenDateInMillis.toDisplayableReminderEditorDate())
        reminderEditorState = reminderEditorState.copy(reminderEditor = updatedDate)
    }

    private fun onPeriodicityDropdownMenuVisibilityUpdate(isVisible: Boolean) {
        reminderEditorState = reminderEditorState.copy(isPeriodicityDropdownMenuExpanded = isVisible)
    }

    private fun onPeriodicityUpdate(periodicity: Periodicity) {
        val updatedPeriodicity = reminderEditorState.reminderEditor.copy(periodicity = periodicity)
        reminderEditorState = reminderEditorState.copy(reminderEditor = updatedPeriodicity)
    }

    private fun getSuccessfulReminderEditorStateToUpsert(localDate: LocalDate): ReminderEditorState {
        val futureDate = localDate.plusDays(1)
        val futureDateInMillis = futureDate.atStartOfDay(dateUtil.zoneIdUTC).toInstant().toEpochMilli()
        val updatedReminderEditorState = reminderEditorState.copy(
            reminderEditor = ReminderEditorUi(
                title = "Mock title",
                is24HourFormat = false,
                reminderEditorTime = DisplayableReminderEditorTime(
                    response = Pair(first = 10, second = 38),
                    displayableResponse = DisplayableTimeResponse(hour = "10", minute = "38"),
                    hourFormat = DisplayableReminderEditorTimeHourFormat(value = "AM", hourFormat = HourFormat.AM)
                ),
                reminderEditorDate = DisplayableReminderEditorDate(
                    value = futureDateInMillis,
                    day = futureDate.dayOfMonth.toString(),
                    month = futureDate.monthValue.toString(),
                    year = futureDate.year.toString()
                ),
                periodicity = Periodicity.DAILY
            )
        )
        return updatedReminderEditorState
    }

    private fun getSuccessfulValidationStatusesToUpsert(): ValidationStatuses {
        val updatedValidationStatuses = validationStatuses.copy(
            title = ValidationStatus.Success,
            time = ValidationStatus.Success,
            date = ValidationStatus.Success
        )
        return updatedValidationStatuses
    }

    @Before
    fun setup() {
        reminderEditorState = ReminderEditorState()
    }

    @Test
    fun initial_ReminderEditorScreen() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                ReminderEditorScreenContent(
                    reminderEditorState = reminderEditorState,
                    onAction = {},
                    onBackClick = { /*TODO*/ },
                    onShowSnackbar = { _,_ -> false }
                )
            }
        }

        backIcon
            .assertIsDisplayed()
            .assertIsEnabled()

        doneIcon
            .assertIsDisplayed()
            .assertIsNotEnabled()

        titleTextField.assertTextContains("")
        titleTextFieldHint
            .assertIsDisplayed()
            .assertTextEquals(titleTextFieldHintText, includeEditableText = false)

        hourAndMinuteTextFieldHints.assertCountEquals(2)
        hourTextField.assertTextEquals("")
        minuteTextField.assertTextEquals("")

        timeTextHourFormat.assertDoesNotExist()

        dateTextFieldHints.all { it.assertIsDisplayed(); true }

        dayTextField.assertTextEquals("")
        monthTextField.assertTextEquals("")
        yearTextField.assertTextEquals("")

        selectedPeriodicityItemTextNode.assertTextEquals(once)
        periodicityMenu.assertDoesNotExist()
    }

    @Test
    fun whenSelectedPastTime_showsPastTimeError() {
        val pastTime = timeUtil.currentTime.minusHours(2)
        androidComposeTestRule.setContent {
            MyReminderTheme {
                ReminderEditorScreenContent(
                    reminderEditorState = reminderEditorState,
                    validationStatuses = validationStatuses,
                    onAction = { action ->
                        reminderEditorActionHandler(
                            action,
                            onPickerDialogUpdate = { pickerDialog ->
                                onPickerDialogUpdate(pickerDialog)
                            },
                            onTimeUpdate = { timeResponse ->
                                onTimeUpdate(timeResponse)
                            },
                            onDateUpdate = { choosenDateInMillis ->
                                onDateUpdate(choosenDateInMillis)
                            },
                            onHandleTimeValidationStatus = {
                                if (reminderEditorState.reminderEditor.reminderEditorDate.value != null)
                                validationStatuses = validationStatuses.copy(time = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TIME))
                            }
                        )
                    },
                    onBackClick = {},
                    onShowSnackbar = { _,_ -> false }
                )
            }
        }

        hourTextField.performClick()

        hourRadioButton.performClick()

        timeUtil.getTimePickerHourNode(localTime = pastTime).performClick()

        minuteRadioButton.performClick()

        timeUtil.getTimePickerMinuteNode(localTime = pastTime).performClick()

        okTextButton.performClick()

        dayTextField.performClick()

        dateUtil
            .getClosestAllowedToSelectDayNode(substring = true)
            .performClick()

        okTextButton.performClick()

        timeTextFieldError
            .assertIsDisplayed()
            .assertTextEquals(timeTextFieldErrorText)
    }

    @Test
    fun whenSelectedWeekend_afterSelectedWeekday_showsOnlyWeekdaysAllowedError() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                ReminderEditorScreenContent(
                    reminderEditorState = reminderEditorState,
                    validationStatuses = validationStatuses,
                    onAction = { action ->
                        reminderEditorActionHandler(
                            action,
                            onPickerDialogUpdate = { pickerDialog ->
                                onPickerDialogUpdate(pickerDialog)
                            },
                            onDateUpdate = { choosenDateInMillis ->
                                onDateUpdate(choosenDateInMillis)
                            },
                            onPeriodicityDropdownMenuVisibilityUpdate = { isVisible ->
                                onPeriodicityDropdownMenuVisibilityUpdate(isVisible)
                            },
                            onPeriodicityUpdate = { periodicity ->
                                onPeriodicityUpdate(periodicity)
                                validationStatuses = validationStatuses.copy(date = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderDateValidation.ONLY_WEEKDAYS_ALLOWED))
                            }
                        )
                    },
                    onBackClick = {},
                    onShowSnackbar = { _,_ -> false }
                )
            }
        }

        dayTextField.performClick()

        dateUtil
            .getClosestWeekendNodes()
            .first()
            .performClick()

        okTextButton.performClick()

        selectedPeriodicityItem.performClick()

        periodicityMenu
            .onChildren()
            .onLast()
            .performClick()

        dateTextFieldError
            .assertIsDisplayed()
            .assertTextEquals(dateTextFieldErrorText)
    }

    @Test
    fun onInsertReminderSuccessfully() {
        val updatedState = getSuccessfulReminderEditorStateToUpsert(localDate = dateUtil.currentDateUTC)
        var isReminderUpserted = false
        var insertedResponse = ""
        var isNavigatedUp = false
        val eventFlow = flowOf(Event.Success(data = ReminderEditorOneTimeEvent.OnUpsertClick(uiText = UiText.StringResource(string.reminderSuccessfullyAdded))))

        androidComposeTestRule.setContent {
            MyReminderTheme {
                ReminderEditorScreenContent(
                    reminderEditorState = updatedState,
                    validationStatuses = getSuccessfulValidationStatusesToUpsert(),
                    eventFlow = eventFlow,
                    onAction = { action ->
                        reminderEditorActionHandler(
                            action,
                            onUpsertReminder = {
                                isReminderUpserted = true
                            }
                        )
                    },
                    onBackClick = { isNavigatedUp = true },
                    onShowSnackbar = { response ,_ ->
                        insertedResponse = response
                        false
                    }
                )
            }
        }
        doneIcon
            .assertIsEnabled()
            .performClick()

        assertThat(isReminderUpserted).isTrue()

        assertThat(insertedResponse).isEqualTo(getString(string.reminderSuccessfullyAdded))

        assertThat(isNavigatedUp).isTrue()
    }

    @Test
    fun onUpdateReminderSuccessfully() {
        val updatedState = getSuccessfulReminderEditorStateToUpsert(localDate = dateUtil.currentDateUTC)
        var isReminderUpserted = false
        var updatedResponse = ""
        var isNavigatedUp = false
        val eventFlow = flowOf(Event.Success(data = ReminderEditorOneTimeEvent.OnUpsertClick(uiText = UiText.StringResource(string.reminderSuccessfullyUpdated))))

        androidComposeTestRule.setContent {
            MyReminderTheme {
                ReminderEditorScreenContent(
                    reminderEditorState = updatedState,
                    validationStatuses = getSuccessfulValidationStatusesToUpsert(),
                    eventFlow = eventFlow,
                    onAction = { action ->
                        reminderEditorActionHandler(
                            action,
                            onUpsertReminder = {
                                isReminderUpserted = true
                            }
                        )
                    },
                    onBackClick = { isNavigatedUp = true },
                    onShowSnackbar = { response ,_ ->
                        updatedResponse = response
                        false
                    }
                )
            }
        }

        doneIcon
            .assertIsEnabled()
            .performClick()

        assertThat(isReminderUpserted).isTrue()

        assertThat(updatedResponse).isEqualTo(getString(string.reminderSuccessfullyUpdated))

        assertThat(isNavigatedUp).isTrue()
    }

}