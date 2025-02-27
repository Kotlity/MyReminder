@file:OptIn(ExperimentalMaterial3Api::class)

package com.kotlity.feature_reminder_editor.composables

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.espresso.device.action.ScreenOrientation
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.feature_reminder_editor.utils.ActivityRecreationManager
import com.kotlity.feature_reminder_editor.composables.date.DateSection
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.models.DisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.utils.DateUtil
import com.kotlity.feature_reminder_editor.utils.FutureSelectableDates
import com.kotlity.feature_reminder_editor.utils.WeekdaysSelectableDates
import com.kotlity.utils.AndroidComposeTestRuleProvider
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Instant

class DateSectionTest: AndroidComposeTestRuleProvider<ComponentActivity>(ComponentActivity::class.java) {

    private lateinit var activityRecreationManager: ActivityRecreationManager<ComponentActivity>
    
    private val dateUtil: DateUtil<ComponentActivity> = DateUtil(androidComposeTestRule)

    private val dateTitle: SemanticsNodeInteraction by lazy { onNodeWithText(dateTitleText) }
    private val dateIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.dateIconDescription)) }
    private val dayTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.dayTextFieldTestTag)) }
    private val monthTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.monthTextFieldTestTag)) }
    private val yearTextField: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.yearTextFieldTestTag)) }
    private val dateTextFieldHints: List<SemanticsNodeInteraction> by lazy { listOf(
        onNodeWithText(getString(string.dayHint)),
        onNodeWithText(getString(string.monthHint)),
        onNodeWithText(getString(string.yearHint))
    ) }
    private val dateTextFieldError: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.dateTextFieldErrorTestTag)) }
    private val datePickerWidget: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.datePickerWidgetTestTag), useUnmergedTree = true) }
    private val datePicker: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.datePickerTestTag)) }
    private val datePickerSelectedDate: SemanticsNodeInteraction by lazy { onNodeWithContentDescription("Current selection:", substring = true) }
    private val datePickerYearSwitcherButton: SemanticsNodeInteraction by lazy { onNodeWithContentDescription("Switch to selecting a year") }
    private val datePickerSwitcherButton: SemanticsNodeInteraction by lazy { onNodeWithContentDescription("Switch to text input mode") }
    private val dateInputSwitcherButton: SemanticsNodeInteraction by lazy { onNodeWithContentDescription("Switch to calendar input mode") }
    private val dateInputEnteredDate: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(dateInputEnteredDateText, substring = true) }
    private val dateInputTextField: SemanticsNodeInteraction by lazy { onNodeWithContentDescription("Date, MM/DD/YYYY") }
    private val dismissTextButton: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.cancel)) }
    private val okTextButton: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.ok)) }

    private val dateTitleText = getString(string.calendar)
    private val dateTextFieldErrorText = getString(string.reminderDateWrongPeriodicity)
    private val datePickerSelectedDateDefaultText = "Selected date"
    private val dateInputEnteredDateText = "Entered date"
    private val dateInputTextFieldLabel = "Date"

    private var pickerDialog by mutableStateOf<PickerDialog?>(null)
    private var displayableReminderEditorDate by mutableStateOf(DisplayableReminderEditorDate())
    private var selectableDates by mutableStateOf<SelectableDates>(FutureSelectableDates())
    private var isError by mutableStateOf(false)
    private var canShowDatePicker by mutableStateOf(true)

    private fun updatePickerDialog(pickerDialog: PickerDialog? = PickerDialog.Date) { this.pickerDialog = pickerDialog }

    @Before
    fun setup() {
        activityRecreationManager = ActivityRecreationManager(androidComposeTestRule)
        pickerDialog = null
        displayableReminderEditorDate = DisplayableReminderEditorDate()
        selectableDates = FutureSelectableDates()
        isError = false
        canShowDatePicker = true
    }

    @Test
    fun initial_dateSectionState() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = {},
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        dateTitle
            .assertIsDisplayed()
            .assertTextEquals(dateTitleText)

        dateIcon.assertIsDisplayed()

        dayTextField.assertTextEquals("")
        monthTextField.assertTextEquals("")
        yearTextField.assertTextEquals("")

        dateTextFieldHints.all { it.assertIsDisplayed(); true }

        dateTextFieldError.assertDoesNotExist()

        datePickerWidget.assertDoesNotExist()
    }

    @Test
    fun onDismissTextButtonClicked_closesDatePickerWidget() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = { updatePickerDialog() },
                    onDateWidgetDismissClick = { updatePickerDialog(null) },
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        dayTextField.performClick()

        datePickerWidget.assertIsDisplayed()

        dismissTextButton.performClick()

        datePickerWidget.assertDoesNotExist()

    }

    @Test
    fun onDayTextFieldClicked_showedDatePicker_appWentToBackgroundAndResumed_keepDisplayingDatePicker() {
        val testLifecycleOwner = TestLifecycleOwner()
        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.STARTED)

        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = { updatePickerDialog() },
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        dayTextField.performClick()

        datePickerWidget.assertIsDisplayed()

        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.CREATED)

        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.RESUMED)

        datePickerWidget.assertIsDisplayed()
    }

    @Test
    fun whenError_dateTextFieldDisplaysIt() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = !isError,
                    errorText = dateTextFieldErrorText,
                    onEditorDateWidgetClick = {},
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        dateTextFieldError
            .assertIsDisplayed()
            .assertTextEquals(dateTextFieldErrorText)
    }

    @Test
    fun afterSelectedDate_displaysReminderDateWrongPeriodicityError() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = if (isError) dateTextFieldErrorText else null,
                    onEditorDateWidgetClick = { updatePickerDialog() },
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = { choosenDate ->
                        updatePickerDialog(null)
                        isError = true
                        displayableReminderEditorDate = choosenDate!!.toDisplayableReminderEditorDate()
                    }
                )
            }
        }

        dayTextField.performClick()

        datePickerWidget.assertIsDisplayed()

        datePicker.assertIsDisplayed()

        datePickerSelectedDate.assertTextEquals(datePickerSelectedDateDefaultText)

        datePickerYearSwitcherButton.assertTextEquals(dateUtil.getDateText())

        val currentDateUTC = dateUtil.currentDateUTC.plusDays(1)

        dateUtil
            .getClosestAllowedToSelectDayNode(localDate = currentDateUTC)
            .performClick()
            .assertIsSelected()

        val selectedDayText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.shortenedMonthDayAndYearFormatter,
            localDate = currentDateUTC
        )

        datePickerSelectedDate.assertTextEquals(selectedDayText)

        okTextButton.performClick()

        dateTextFieldError
            .assertIsDisplayed()
            .assertTextEquals(dateTextFieldErrorText)

        dateTextFieldHints.all { it.assertDoesNotExist(); true }

        val dayResponse = displayableReminderEditorDate.day!!
        val monthResponse = displayableReminderEditorDate.month!!
        val yearResponse = displayableReminderEditorDate.year!!

        dayTextField.assertTextEquals(dayResponse)
        monthTextField.assertTextEquals(monthResponse)
        yearTextField.assertTextEquals(yearResponse)

    }

    @Test
    fun tryingToSelectPreviousYears_doesNothing() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = PickerDialog.Date,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = {},
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        datePickerYearSwitcherButton.performClick()

        dateUtil.getCurrentYearNode().assertIsSelected()

        dateUtil.getPreviousYearNodes().all { it.assertIsNotEnabled(); true }
    }

    @Test
    fun tryingToSelectPreviousDays_doesNothing() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = PickerDialog.Date,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = {},
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        dateUtil.getCurrentDayNode().assertIsEnabled()

        dateUtil.getPreviousDayNodes().all { it.assertIsNotEnabled(); true }
    }

    @Test
    fun tryingToSelectWeekendDays_doesNothing() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = PickerDialog.Date,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = WeekdaysSelectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = {},
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        dateUtil.getClosestWeekendNodes().all { it.assertIsNotEnabled(); true }
    }

    @Test
    fun onPortraitMode_onYearTextFieldClicked_showsModalDatePicker_datePickerSwitcherButtonClicked_showsDatePickerInput_onDismissTextButtonClicked_closesDatePickerWidget() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = { updatePickerDialog() },
                    onDateWidgetDismissClick = { updatePickerDialog(null) },
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        yearTextField.performClick()

        datePickerSwitcherButton
            .assertIsDisplayed()
            .performClick()

        dateInputTextField
            .assertIsDisplayed()
            .assertTextEquals("Date", includeEditableText = false)

        dateInputSwitcherButton.assertIsDisplayed()

        dismissTextButton.performClick()

        datePickerWidget.assertDoesNotExist()
    }

    @Test
    fun withPortraitOrientation_displaysModalDatePicker_afterChangingToLandscapeOrientation_displaysDatePickerInputWithSavedInput() {
        activityRecreationManager.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = { updatePickerDialog() },
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_PORTRAIT)

        dayTextField.performClick()

        datePickerYearSwitcherButton.performClick()

        dateUtil.getNextYearNode().performClick()

        val localDate = dateUtil.currentDateUTC.plusYears(1).plusDays(1)

        val selectedShortestDate = dateUtil.getDateText(localDate = localDate)

        datePickerYearSwitcherButton.assertTextEquals(selectedShortestDate)

        dateUtil
            .getClosestAllowedToSelectDayNode(localDate = localDate)
            .performClick()
            .assertIsSelected()

        activityRecreationManager.recreateWith {
            changeScreenOrientation(ScreenOrientation.LANDSCAPE)
            canShowDatePicker = false
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        dateInputSwitcherButton.assertDoesNotExist()

        val dateInputEnteredDateText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.shortenedMonthDayAndYearFormatter,
            localDate = localDate
        )

        dateInputEnteredDate.assertTextEquals(dateInputEnteredDateText)

        val dateInputTextFieldText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.typedDateInDateInputTextFieldDateFormatter,
            localDate = localDate
        )
        dateInputTextField.assertTextContains(dateInputTextFieldText)
    }

    @Test
    fun withLandscapeOrientation_displaysDatePickerInput_afterChangingToPortraitOrientation_keepDisplayingDatePickerInputWithSavedInput() {
        changeScreenOrientation(ScreenOrientation.LANDSCAPE)
        canShowDatePicker = false

        activityRecreationManager.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = { updatePickerDialog() },
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = {}
                )
            }
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        dayTextField.performClick()

        dateInputSwitcherButton.assertDoesNotExist()

        dateInputEnteredDate
            .assertIsDisplayed()
            .assertTextEquals(dateInputEnteredDateText)

        dateInputTextField
            .assertIsDisplayed()
            .assertTextEquals(dateInputTextFieldLabel, includeEditableText = false)

        val localDate = dateUtil.currentDateUTC.plusDays(5)

        val typedDateText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.typedDateInDateInputTextFieldDateFormatter,
            localDate = localDate
        )

        dateInputTextField.performClick()

        typedDateText.forEach {
            dateInputTextField.performTextInput(it.toString())
        }

        dateInputTextField.assertTextContains(typedDateText)

        val dateInputEnteredDateText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.shortenedMonthDayAndYearFormatter,
            localDate = localDate
        )

        dateInputEnteredDate.assertTextEquals(dateInputEnteredDateText)

        activityRecreationManager.recreateWith {
            changeScreenOrientation(ScreenOrientation.PORTRAIT)
            canShowDatePicker = true
        }

        dateInputSwitcherButton.assertIsDisplayed()

        dateInputTextField.assertTextContains(typedDateText)

        dateInputEnteredDate.assertTextEquals(dateInputEnteredDateText)
    }

    @Test
    fun afterSelectedTime_rotatesScreen_SavedInput() {
        activityRecreationManager.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = selectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = { updatePickerDialog() },
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = { choosenDate ->
                        updatePickerDialog(null)
                        displayableReminderEditorDate = choosenDate!!.toDisplayableReminderEditorDate()
                    }
                )
            }
        }

        dayTextField.performClick()

        dateUtil
            .getClosestAllowedToSelectDayNode()
            .performClick()

        okTextButton.performClick()

        dateTextFieldHints.all { it.assertDoesNotExist(); true }

        dateTextFieldError.assertDoesNotExist()

        val dayResponse = displayableReminderEditorDate.day!!
        val monthResponse = displayableReminderEditorDate.month!!
        val yearResponse = displayableReminderEditorDate.year!!

        dayTextField.assertTextEquals(dayResponse)

        monthTextField.assertTextEquals(monthResponse)

        yearTextField.assertTextEquals(yearResponse)

        activityRecreationManager.recreateWith { changeScreenOrientation(ScreenOrientation.LANDSCAPE) }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        dateTextFieldHints.all { it.assertDoesNotExist(); true }

        dateTextFieldError.assertDoesNotExist()

        dayTextField.assertTextEquals(dayResponse)

        monthTextField.assertTextEquals(monthResponse)

        yearTextField.assertTextEquals(yearResponse)
    }

    @Test
    fun typedNewDate_updatesCurrentDateInTheTextFields() {
        val saturdayLocalDate = dateUtil.getSaturdayLocalDate()
        val currentDateInMillis = com.kotlity.utils.DateUtil.findClosestWeekdayInMillis(localDate = saturdayLocalDate)
        val currentDate = Instant.ofEpochMilli(currentDateInMillis).atZone(dateUtil.zoneIdUTC).toLocalDate()

        displayableReminderEditorDate = currentDateInMillis.toDisplayableReminderEditorDate()

        val dayResponse = displayableReminderEditorDate.day!!
        val monthResponse = displayableReminderEditorDate.month!!
        val yearResponse = displayableReminderEditorDate.year!!

        androidComposeTestRule.setContent {
            MyReminderTheme {
                DateSection(
                    pickerDialog = pickerDialog,
                    canShowDatePicker = canShowDatePicker,
                    displayableReminderEditorDate = displayableReminderEditorDate,
                    selectableDates = WeekdaysSelectableDates,
                    isError = isError,
                    errorText = null,
                    onEditorDateWidgetClick = { updatePickerDialog() },
                    onDateWidgetDismissClick = {},
                    onDateWidgetConfirmClick = { choosenDate ->
                        updatePickerDialog(null)
                        displayableReminderEditorDate = choosenDate!!.toDisplayableReminderEditorDate()
                    }
                )
            }
        }

        dateTextFieldHints.all { it.assertDoesNotExist(); true }

        dateTextFieldError.assertDoesNotExist()

        dayTextField.assertTextEquals(dayResponse)

        monthTextField.assertTextEquals(monthResponse)

        yearTextField.assertTextEquals(yearResponse)

        monthTextField.performClick()

        val selectedDayResponseText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.shortenedMonthDayAndYearFormatter,
            localDate = currentDate
        )

        val yearSwitcherButtonText = dateUtil.getDateText(localDate = currentDate)

        val selectedDayInputResponseText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.typedDateInDateInputTextFieldDateFormatter,
            localDate = currentDate
        )

        datePickerSelectedDate.assertTextEquals(selectedDayResponseText)

        datePickerYearSwitcherButton.assertTextEquals(yearSwitcherButtonText)

        dateUtil.getClosestAllowedToSelectDayNode(
            localDate = currentDate,
            isWeekendDaysAllowed = false
        ).assertIsSelected()

        datePickerSwitcherButton
            .assertIsDisplayed()
            .performClick()

        dateInputSwitcherButton.assertIsDisplayed()

        dateInputEnteredDate.assertTextEquals(selectedDayResponseText)

        dateInputTextField.assertTextContains(selectedDayInputResponseText)

        val updatedLocalDate = currentDate.plusDays(2)

        val updatedDateText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.typedDateInDateInputTextFieldDateFormatter,
            localDate = updatedLocalDate
        )

        val updatedDateInputEnteredDateText = dateUtil.getDateText(
            dateTimeFormatter = dateUtil.shortenedMonthDayAndYearFormatter,
            localDate = updatedLocalDate
        )

        dateInputTextField
            .performClick()
            .performTextClearance()

        updatedDateText.forEach {
            dateInputTextField.performTextInput(it.toString())
        }

        dateInputTextField.assertTextContains(updatedDateText)

        dateInputEnteredDate.assertTextEquals(updatedDateInputEnteredDateText)

        okTextButton.performClick()

        val updatedDayResponse = displayableReminderEditorDate.day!!
        val updatedMonthResponse = displayableReminderEditorDate.month!!
        val updatedYearResponse = displayableReminderEditorDate.year!!

        dayTextField.assertTextEquals(updatedDayResponse)

        monthTextField.assertTextEquals(updatedMonthResponse)

        yearTextField.assertTextEquals(updatedYearResponse)
    }
}