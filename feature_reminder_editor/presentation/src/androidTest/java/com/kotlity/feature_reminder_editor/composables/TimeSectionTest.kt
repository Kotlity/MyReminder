package com.kotlity.feature_reminder_editor.composables

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.espresso.device.action.ScreenOrientation
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.feature_reminder_editor.utils.ActivityRecreationManager
import com.kotlity.feature_reminder_editor.composables.time.TimeInputWidgetResourceProvider
import com.kotlity.feature_reminder_editor.composables.time.TimePickerWidgetResourceProvider
import com.kotlity.feature_reminder_editor.composables.time.TimeSection
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorTimeHourFormat
import com.kotlity.feature_reminder_editor.models.DisplayableTimeResponse
import com.kotlity.feature_reminder_editor.models.HourFormat
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.utils.AndroidComposeTestRuleProvider
import org.junit.Before
import org.junit.Test

class TimeSectionTest: AndroidComposeTestRuleProvider<ComponentActivity>(ComponentActivity::class.java) {

    private lateinit var activityRecreationManager: ActivityRecreationManager<ComponentActivity>

    private val timeTitle: SemanticsNodeInteraction by lazy { onNodeWithText(timeTitleText) }
    private val timeIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.timeIconDescription)) }
    private val timeTextHourFormat: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.timeTextHourFormatTestTag)) }
    private val hourTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.hourTextFieldTestTag)) }
    private val minuteTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.minuteTextFieldTestTag)) }
    private val hourAndMinuteTextFieldHints: SemanticsNodeInteractionCollection by lazy { androidComposeTestRule.onAllNodesWithText(getString(string.timeHourAndMinuteHint)) }
    private val timeTextFieldError: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.timeTextFieldErrorTestTag)) }
    private val timePickerWidget: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.timePickerWidgetTestTag), useUnmergedTree = true) }
    private val timePickerWidgetTitle: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.timePickerWidgetTitleTestTag)) }
    private val timePicker: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.timePickerTestTag)) }
    private val timeInput: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.timeInputTestTag)) }
    private val timeInputHourEditableText: SemanticsNodeInteraction by lazy { onNodeWithContentDescription("for hour") }
    private val timeInputMinuteEditableText: SemanticsNodeInteraction by lazy { onNodeWithContentDescription("for minutes") }
    private val timePickerToggleIconButton: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(TimePickerWidgetResourceProvider().description)) }
    private val timeInputToggleIconButton: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(TimeInputWidgetResourceProvider().description)) }
    private val dismissTextButton: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.cancel)) }
    private val okTextButton: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.ok)) }

    private val amTab: SemanticsNodeInteraction by lazy { androidComposeTestRule.onNode(hasTextExactly("AM") and isSelectable()) }
    private val pmTab: SemanticsNodeInteraction by lazy { androidComposeTestRule.onNode(hasTextExactly("PM") and isSelectable()) }

    private val timeTitleText = getString(string.time)
    private val hourAndMinuteTextFieldHintText = getString(string.timeHourAndMinuteHint)
    private val timePickerWidgetTitleText = getString(string.selectTimeTitle)
    private val timeInputWidgetTitleText = getString(string.enterTimeTitle)
    private val timeTextFieldErrorText = getString(string.reminderTimePastTense)

    private var displayableReminderEditorTime by mutableStateOf(DisplayableReminderEditorTime())
    private var pickerDialog by mutableStateOf<PickerDialog?>(null)
    private var is24HourFormat by mutableStateOf(true)
    private var isError by mutableStateOf(false)
    private var canShowToggleIconButton by mutableStateOf(true)

    private fun updatePickerDialog(pickerDialog: PickerDialog? = PickerDialog.Time.TIME_PICKER) { this.pickerDialog = pickerDialog }

    private val allComponentsWithRadioButtonRole: SemanticsNodeInteractionCollection by lazy {
        val hasRadioButtonRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
        androidComposeTestRule.onAllNodes(hasRadioButtonRole)
    }

    private val hoursGraph by lazy {
        androidComposeTestRule
            .onAllNodesWithContentDescription("hours")
            .fetchSemanticsNodes()
    }

    private val minutesGraph by lazy {
        androidComposeTestRule
            .onAllNodesWithContentDescription("minutes")
            .fetchSemanticsNodes()
    }

    @Before
    fun setup() {
        activityRecreationManager = ActivityRecreationManager(androidComposeTestRule)
        displayableReminderEditorTime = DisplayableReminderEditorTime()
        pickerDialog = null
        is24HourFormat = true
        isError = false
        canShowToggleIconButton = true
    }

    @Test
    fun initial_TimeSectionState() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = {},
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        timeTitle
            .assertIsDisplayed()
            .assertTextEquals(timeTitleText)

        timeIcon.assertIsDisplayed()

        timeTextHourFormat.assertDoesNotExist()

        hourTextField.assertTextEquals("")

        minuteTextField.assertTextEquals("")

        hourAndMinuteTextFieldHints
            .assertCountEquals(2)
            .assertAll(hasTextExactly(hourAndMinuteTextFieldHintText))

        timeTextFieldError.assertDoesNotExist()

        timePickerWidget.assertDoesNotExist()
    }

    @Test
    fun is24HourFormatIsFalse_hourFormatSettedToPm_and_TimeTextHourFormatDisplaysIt() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime.copy(
                        hourFormat = DisplayableReminderEditorTimeHourFormat(value = "PM", hourFormat = HourFormat.PM)
                    ),
                    is24HourFormat = !is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = {},
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        timeTextHourFormat
            .assertIsDisplayed()
            .assertTextEquals("PM")
    }

    @Test
    fun whenError_timeTextFieldDisplaysIt() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = !isError,
                    errorText = timeTextFieldErrorText,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = {},
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        timeTextFieldError
            .assertIsDisplayed()
            .assertTextEquals(timeTextFieldErrorText)
    }

    @Test
    fun afterSelectedTime_displaysPastTenseError() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = if (isError) timeTextFieldErrorText else null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog() },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = { timeResponse ->
                        updatePickerDialog(null)
                        displayableReminderEditorTime = timeResponse.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
                    }
                )
            }
        }

        timeTextFieldError.assertDoesNotExist()

        hourAndMinuteTextFieldHints.assertCountEquals(2)

        hourTextField.performClick()

        allComponentsWithRadioButtonRole
            .onLast()
            .performClick()
            .assertIsSelected()

        hoursGraph.isEmpty()
        minutesGraph.isNotEmpty()

        onNodeWithContentDescription("50 minutes").performClick()

        allComponentsWithRadioButtonRole
            .onFirst()
            .performClick()
            .assertIsSelected()

        hoursGraph.isNotEmpty()
        minutesGraph.isEmpty()

        onNodeWithContentDescription("14 hours").performClick()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertTextEquals("14")

        allComponentsWithRadioButtonRole
            .onLast()
            .assertTextEquals("50")

        okTextButton.performClick()

        isError = true

        hourTextField.assertTextEquals("14")
        minuteTextField.assertTextEquals("50")

        timeTextFieldError
            .assertIsDisplayed()
            .assertTextEquals(timeTextFieldErrorText)

    }

    @Test
    fun onHourTextFieldClicked_showedTimePicker_appWentToBackgroundAndResumed_keepDisplayingTimePicker() {
        val testLifecycleOwner = TestLifecycleOwner()
        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.STARTED)

        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog() },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        hourTextField.performClick()

        timePickerWidget.assertIsDisplayed()

        timePicker.assertIsDisplayed()

        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.CREATED)

        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.RESUMED)

        timePickerWidget.assertIsDisplayed()

        timePicker.assertIsDisplayed()
    }

    @Test
    fun onPortraitMode_onHourTextFieldClicked_showsTimePickerWidgetWithTimePicker_timeToggleIconButtonDisplaysTimeInputIcon_onDismissIconButtonClicked_closesTimePickerWidget() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog() },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = { pickerDialog = null },
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        timePickerWidget.assertDoesNotExist()

        hourTextField.performClick()

        timePickerWidget.assertIsDisplayed()

        assertThat(pickerDialog).isEqualTo(PickerDialog.Time.TIME_PICKER)

        timePickerWidgetTitle.assertTextEquals(timePickerWidgetTitleText)

        timePickerToggleIconButton.assertIsDisplayed()

        timeInputToggleIconButton.assertDoesNotExist()

        dismissTextButton
            .assertHasClickAction()
            .performClick()

        assertThat(pickerDialog).isEqualTo(null)

        timePickerWidget.assertDoesNotExist()
    }

    @Test
    fun initialWith24HourFormat_displaysZeroZeroAsHourAndZeroZeroAsMinute_andNotDisplayedAmAndPmTabs() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = PickerDialog.Time.TIME_PICKER,
                    onEditorTimeWidgetClick = {},
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        allComponentsWithRadioButtonRole
            .assertCountEquals(2)
            .assertAll(hasTextExactly("00"))

        amTab.assertDoesNotExist()

        pmTab.assertDoesNotExist()
    }

    @Test
    fun initialWith12HourFormat_displays12AsHourAndZeroZeroAsMinute_andDisplaysAmAndPmTabs() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = !is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = PickerDialog.Time.TIME_PICKER,
                    onEditorTimeWidgetClick = {},
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertTextEquals("12")

        allComponentsWithRadioButtonRole
            .onLast()
            .assertTextEquals("00")

        amTab
            .assertIsDisplayed()
            .assertIsSelected()

        pmTab
            .assertIsDisplayed()
            .assertIsNotSelected()
    }

    @Test
    fun with12HourFormat_opensTimePickerWidget_chooses10Hour30MinuteAM_onConfirmButtonClicked_closedTimePickerWidget_hideTimeAndHourTextFieldHints_andUpdatesTimeAndHourTextFields() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = !is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog() },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = { timeResponse ->
                        updatePickerDialog(null)
                        displayableReminderEditorTime = timeResponse.toDisplayableReminderEditorTime(is24HourFormat = false)
                    }
                )
            }
        }

        minuteTextField.performClick()

        onNodeWithContentDescription("10 o'clock").performClick()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertIsSelected()
            .assertTextEquals("10")

        allComponentsWithRadioButtonRole
            .onLast()
            .performClick()
            .assertIsSelected()
            .assertTextEquals("00")

        onNodeWithContentDescription("30 minutes").performClick()

        allComponentsWithRadioButtonRole
            .onLast()
            .assertTextEquals("30")

        amTab
            .assertIsDisplayed()
            .assertIsSelected()

        pmTab
            .assertIsDisplayed()
            .assertIsNotSelected()

        okTextButton.performClick()

        timePickerWidget.assertDoesNotExist()

        timeTextHourFormat
            .assertIsDisplayed()
            .assertTextEquals("AM")

        hourTextField.assertTextEquals("10")

        minuteTextField.assertTextEquals("30")

        hourAndMinuteTextFieldHints.assertCountEquals(0)
    }

    @Test
    fun with12HourFormat_opensTimePickerWidget_chooses5Hour5MinutePM_onConfirmButtonClicked_closedTimePickerWidget_hideTimeAndHourTextFieldHints_andUpdatesTimeAndHourTextFields() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = !is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog() },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = { timeResponse ->
                        updatePickerDialog(null)
                        displayableReminderEditorTime = timeResponse.toDisplayableReminderEditorTime(is24HourFormat = false)
                    }
                )
            }
        }

        hourTextField.performClick()

        onNodeWithContentDescription("5 o'clock").performClick()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertIsSelected()
            .assertTextEquals("05")

        allComponentsWithRadioButtonRole
            .onLast()
            .performClick()
            .assertIsSelected()
            .assertTextEquals("00")

        onNodeWithContentDescription("5 minutes").performClick()

        allComponentsWithRadioButtonRole
            .onLast()
            .assertTextEquals("05")

        pmTab.performClick()

        pmTab
            .assertIsDisplayed()
            .assertIsSelected()

        amTab
            .assertIsDisplayed()
            .assertIsNotSelected()

        okTextButton.performClick()

        timePickerWidget.assertDoesNotExist()

        timeTextHourFormat
            .assertIsDisplayed()
            .assertTextEquals("PM")

        hourTextField.assertTextEquals("05")

        minuteTextField.assertTextEquals("05")

        hourAndMinuteTextFieldHints.assertCountEquals(0)
    }

    @Test
    fun withPortraitOrientation_displaysTimePicker_afterChangingToLandscapeOrientation_displaysTimeInputWithSavedInput() {
        activityRecreationManager.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog() },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_PORTRAIT)

        hourTextField.performClick()

        timePicker.assertIsDisplayed()
        timePickerToggleIconButton.assertIsDisplayed()

        timeInput.assertDoesNotExist()
        timeInputToggleIconButton.assertDoesNotExist()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertIsSelected()
            .assertTextEquals("00")

        onNodeWithContentDescription("23 hours").performClick()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertIsSelected()
            .assertTextEquals("23")

        allComponentsWithRadioButtonRole
            .onLast()
            .performClick()
            .assertIsSelected()
            .assertTextEquals("00")

        onNodeWithContentDescription("55 minutes").performClick()

        allComponentsWithRadioButtonRole
            .onLast()
            .assertIsSelected()
            .assertTextEquals("55")

        activityRecreationManager.recreateWith {
            changeScreenOrientation(ScreenOrientation.LANDSCAPE)
            updatePickerDialog(PickerDialog.Time.TIME_INPUT)
            canShowToggleIconButton = false
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        timePicker.assertDoesNotExist()
        timePickerToggleIconButton.assertDoesNotExist()

        timeInput.assertIsDisplayed()
        timeInputToggleIconButton.assertDoesNotExist()

        timeInputHourEditableText.assertTextEquals("23")

        timeInputMinuteEditableText.assertTextEquals("55")
    }

    @Test
    fun withLandscapeOrientation_displaysTimeInput_afterChangingToPortraitOrientation_keepDisplayingTimeInputWithSavedInput() {
        changeScreenOrientation(ScreenOrientation.LANDSCAPE)
        canShowToggleIconButton = false

        activityRecreationManager.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = !is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog(PickerDialog.Time.TIME_INPUT) },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        minuteTextField.performClick()

        timeInput.assertIsDisplayed()
        timePicker.assertDoesNotExist()

        timePickerWidgetTitle.assertTextEquals(timeInputWidgetTitleText)
        timeInputToggleIconButton.assertDoesNotExist()

        amTab
            .assertIsDisplayed()
            .assertIsSelected()

        pmTab
            .assertIsDisplayed()
            .assertIsNotSelected()

        timeInputHourEditableText.assertTextEquals("12")

        timeInputMinuteEditableText.assertTextEquals("00")

        androidComposeTestRule.waitForIdle()

        timeInputHourEditableText.performTextReplacement("05")

        timeInputMinuteEditableText
            .performClick()
            .performTextReplacement("50")

        androidComposeTestRule.waitForIdle()

        pmTab
            .performClick()
            .assertIsSelected()

        activityRecreationManager.recreateWith {
            changeScreenOrientation(ScreenOrientation.PORTRAIT)
            canShowToggleIconButton = true
        }

        timeInput.assertIsDisplayed()
        timePicker.assertDoesNotExist()

        timePickerWidgetTitle.assertTextEquals(timeInputWidgetTitleText)
        timeInputToggleIconButton.assertIsDisplayed()

        timeInputHourEditableText.assertTextEquals("05")

        timeInputMinuteEditableText.assertTextEquals("50")

        amTab
            .assertIsDisplayed()
            .assertIsNotSelected()

        pmTab
            .assertIsDisplayed()
            .assertIsSelected()
    }

    @Test
    fun afterSelectedTime_rotatesScreen_SavedInput() {
        activityRecreationManager.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog() },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = { timeResponse ->
                        updatePickerDialog(null)
                        displayableReminderEditorTime = timeResponse.toDisplayableReminderEditorTime(is24HourFormat = true)
                    }
                )
            }
        }

        minuteTextField.performClick()

        timePickerWidget.assertIsDisplayed()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertIsSelected()
            .assertTextEquals("00")

        allComponentsWithRadioButtonRole
            .onLast()
            .assertIsNotSelected()
            .assertTextEquals("00")

        onNodeWithContentDescription("18 hours").performClick()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertIsSelected()
            .assertTextEquals("18")

        allComponentsWithRadioButtonRole
            .onLast()
            .performClick()
            .assertIsSelected()

        onNodeWithContentDescription("45 minutes").performClick()

        allComponentsWithRadioButtonRole
            .onLast()
            .assertIsSelected()
            .assertTextEquals("45")

        okTextButton.performClick()

        timePickerWidget.assertDoesNotExist()

        timeTextHourFormat.assertDoesNotExist()

        hourAndMinuteTextFieldHints.assertCountEquals(0)

        val hourResponse = displayableReminderEditorTime.displayableResponse.hour!!
        val minuteResponse = displayableReminderEditorTime.displayableResponse.minute!!

        hourTextField.assertTextEquals(hourResponse)

        minuteTextField.assertTextEquals(minuteResponse)

        activityRecreationManager.recreateWith { changeScreenOrientation(ScreenOrientation.LANDSCAPE) }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        hourTextField.assertTextEquals(hourResponse)

        minuteTextField.assertTextEquals(minuteResponse)
    }

    @Test
    fun afterSelectedTime_rotatesScreenSavesInput_changedIs24HourFormat_formattedTimeInputInTextFields() {
        activityRecreationManager.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog() },
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = { timeResponse ->
                        updatePickerDialog(null)
                        displayableReminderEditorTime = timeResponse.toDisplayableReminderEditorTime(is24HourFormat = true)
                    }
                )
            }
        }

        minuteTextField.performClick()

        onNodeWithContentDescription("22 hours").performClick()

        allComponentsWithRadioButtonRole
            .onLast()
            .performClick()

        onNodeWithContentDescription("25 minutes").performClick()

        okTextButton.performClick()

        timeTextHourFormat.assertDoesNotExist()

        hourAndMinuteTextFieldHints.assertCountEquals(0)

        hourTextField.assertTextEquals("22")

        minuteTextField.assertTextEquals("25")

        activityRecreationManager.recreateWith {
            changeScreenOrientation(ScreenOrientation.LANDSCAPE)
            is24HourFormat = false
            displayableReminderEditorTime = displayableReminderEditorTime.response.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        timeTextHourFormat
            .assertIsDisplayed()
            .assertTextEquals("PM")

        hourTextField.assertTextEquals("10")

        minuteTextField.assertTextEquals("25")
    }

    @Test
    fun afterSelectedTime_inTimePicker_changesIs24HourFormat_displaysPickerAccordingToHourFormat() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = PickerDialog.Time.TIME_PICKER,
                    onEditorTimeWidgetClick = {},
                    onToggleTimePickerWidgetClick = {},
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = {}
                )
            }
        }

        amTab.assertDoesNotExist()
        pmTab.assertDoesNotExist()

        hoursGraph.isNotEmpty()

        onNodeWithContentDescription("15 hours").performClick()

        allComponentsWithRadioButtonRole
            .onLast()
            .performClick()
            .assertIsSelected()

        minutesGraph.isNotEmpty()

        onNodeWithContentDescription("50 minutes").performClick()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertTextEquals("15")

        allComponentsWithRadioButtonRole
            .onLast()
            .assertTextEquals("50")

        is24HourFormat = false

        amTab
            .assertIsDisplayed()
            .assertIsNotSelected()

        pmTab
            .assertIsDisplayed()
            .assertIsSelected()

        allComponentsWithRadioButtonRole
            .onFirst()
            .assertTextEquals("03")
            .assertIsNotSelected()

        allComponentsWithRadioButtonRole
            .onLast()
            .assertTextEquals("50")
            .assertIsSelected()
    }

    @Test
    fun selectedNewTime_updatesCurrentTimeInTheTextFields() {
        displayableReminderEditorTime = DisplayableReminderEditorTime(
            response = Pair(first = 21, second = 13),
            displayableResponse = DisplayableTimeResponse(hour = "09", minute = "13"),
            hourFormat = DisplayableReminderEditorTimeHourFormat(value = "PM", hourFormat = HourFormat.PM)
        )
        is24HourFormat = false

        androidComposeTestRule.setContent {
            MyReminderTheme {
                TimeSection(
                    displayableReminderEditorTime = displayableReminderEditorTime,
                    is24HourFormat = is24HourFormat,
                    canShowToggleIconButton = canShowToggleIconButton,
                    isError = isError,
                    errorText = null,
                    pickerDialog = pickerDialog,
                    onEditorTimeWidgetClick = { updatePickerDialog(PickerDialog.Time.TIME_INPUT) },
                    onToggleTimePickerWidgetClick = { updatePickerDialog() },
                    onTimePickerWidgetDismissClick = {},
                    onTimePickerWidgetConfirmClick = { timeResponse ->
                        updatePickerDialog(null)
                        displayableReminderEditorTime = timeResponse.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
                    }
                )
            }
        }

        hourAndMinuteTextFieldHints.assertCountEquals(0)

        hourTextField.assertTextEquals("09")
        minuteTextField.assertTextEquals("13")

        timeTextHourFormat
            .assertIsDisplayed()
            .assertTextEquals("PM")

        minuteTextField.performClick()

        timePickerWidget.assertIsDisplayed()

        timePickerWidgetTitle.assertTextEquals(timeInputWidgetTitleText)

        timeInput.assertIsDisplayed()

        timeInputToggleIconButton.assertIsDisplayed()

        timeInputHourEditableText
            .assertTextEquals("09")
            .assertIsFocused()

        timeInputMinuteEditableText
            .assertTextEquals("13")
            .assertIsNotFocused()

        amTab.assertIsDisplayed()

        pmTab
            .assertIsDisplayed()
            .assertIsSelected()

        timeInputHourEditableText.performTextReplacement("12")

        timeInputMinuteEditableText
            .performClick()
            .assertIsFocused()
            .performTextReplacement("42")

        amTab
            .performClick()
            .assertIsSelected()

        pmTab.assertIsNotSelected()

        timeInputHourEditableText.assertTextEquals("12")
        timeInputMinuteEditableText.assertTextEquals("42")

        okTextButton.performClick()

        timeTextHourFormat.assertTextEquals("AM")

        hourTextField.assertTextEquals("12")
        minuteTextField.assertTextEquals("42")
    }
}