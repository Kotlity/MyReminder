package com.kotlity.feature_reminder_editor

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kotlity.TimeFormatter
import com.kotlity.core.Periodicity
import com.kotlity.core.Reminder
import com.kotlity.core.resources.R.*
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.AlarmValidationError
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.Event
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.ValidationStatus
import com.kotlity.core.util.Validator
import com.kotlity.core.util.toString
import com.kotlity.di.testTimeFormatterModule
import com.kotlity.feature_reminder_editor.actions.ReminderEditorAction
import com.kotlity.feature_reminder_editor.di.testReminderEditorRepositoryModule
import com.kotlity.feature_reminder_editor.events.ReminderEditorOneTimeEvent
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorDate
import com.kotlity.feature_reminder_editor.mappers.toDisplayableReminderEditorTime
import com.kotlity.feature_reminder_editor.mappers.toReminderEditorUi
import com.kotlity.feature_reminder_editor.models.PickerDialog
import com.kotlity.feature_reminder_editor.models.ReminderEditorUi
import com.kotlity.feature_reminder_editor.states.ReminderEditorState
import com.kotlity.utils.AndroidKoinDependencyProvider
import com.kotlity.utils.TestRuleProvider
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

private val mockReminders = (1..10).map { index ->
    Reminder(
        id = index.toLong(),
        title = "title$index",
        reminderTime = System.currentTimeMillis() + index.toLong() * 3600 * 1000,
        periodicity = if (index % 2 == 0) Periodicity.WEEKDAYS else Periodicity.ONCE
    )
}

@SmallTest
@OptIn(ExperimentalCoroutinesApi::class)
class ReminderEditorViewModelTest: AndroidKoinDependencyProvider(
    modules = listOf(
        testReminderEditorRepositoryModule,
        testTimeFormatterModule
    )
), TestRuleProvider {

    override val coroutineDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()

    @MockK
    private lateinit var titleValidator: Validator<String, AlarmValidationError.AlarmTitleValidation>

    @MockK
    private lateinit var timeValidator: Validator<Long, AlarmValidationError.AlarmReminderTimeValidation>

    private val testReminderEditorRepository by inject<TestReminderEditorRepository>()
    private val testTimeFormatter by inject<TimeFormatter>()

    private lateinit var context: Context

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var reminderEditorViewModel: ReminderEditorViewModel

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val mockId: Long = 3
        savedStateHandle = SavedStateHandle(mapOf("id" to mockId))
        reminderEditorViewModel = ReminderEditorViewModel(
            savedStateHandle = savedStateHandle,
            reminderEditorRepository = testReminderEditorRepository,
            timeFormatter = testTimeFormatter,
            titleValidator = titleValidator,
            timeValidator = timeValidator
        )
    }

    @Test
    fun initially_equals_to_default_ReminderEditorState() = runTest {
        savedStateHandle["id"] = null
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val state = reminderEditorViewModel.reminderEditorState.value

        assertThat(state).isEqualTo(ReminderEditorState())
    }

    @Test
    fun retrieve_id_load_reminder_and_update_ReminderEditorState() = runTest {
        val mockId: Long =  3
        val is24HourFormat = testTimeFormatter.is24HourFormat.first()
        val expectedReminder = mockReminders.find { it.id == mockId }!!.toReminderEditorUi(is24HourFormat = is24HourFormat)

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val updatedReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        assertThat(updatedReminderEditorState.reminderEditor).isEqualTo(expectedReminder)
    }

    @Test
    fun retrieve_id_load_reminder_unsuccessfully_and_send_error_to_channel() = runTest {
        val expectedError = ReminderError.Database(error = DatabaseError.SQLITE_EXCEPTION)
        testReminderEditorRepository.updateError(error = expectedError)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val errorFlow = reminderEditorViewModel.eventFlow.first()
        assertThat(errorFlow.isError).isTrue()
        assertThat(errorFlow.getError).isEqualTo(expectedError)
    }

    @Test
    fun change_is_24hour_format_when_inserting_a_new_reminder_successfully_updates_ReminderEditorState() = runTest {
        savedStateHandle["id"] = null

        val is24HourFormatUpdates = mutableListOf<Boolean>()
        backgroundScope.launch(coroutineDispatcher) { testTimeFormatter.is24HourFormat.toList(is24HourFormatUpdates) }

        assertThat(is24HourFormatUpdates[0]).isTrue()

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialReminderEditorState = ReminderEditorState()
        assertThat(reminderEditorViewModel.reminderEditorState.value).isEqualTo(initialReminderEditorState)

        testTimeFormatter.is24HourFormatChanged(update = false)

        assertThat(is24HourFormatUpdates[1]).isFalse()

        val updatedReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        val expectedUpdatedReminderEditorState = ReminderEditorState(reminderEditor = ReminderEditorUi(is24HourFormat = false))
        assertThat(updatedReminderEditorState).isEqualTo(expectedUpdatedReminderEditorState)
    }

    @Test
    fun change_is_24hour_format_when_updating_a_reminder_successfully_updates_ReminderEditorState() = runTest {
        val mockId: Long = 5
        savedStateHandle["id"] = mockId
        val is24HourFormatUpdates = mutableListOf<Boolean>()
        testTimeFormatter.is24HourFormatChanged(update = false)
        backgroundScope.launch(coroutineDispatcher) { testTimeFormatter.is24HourFormat.toList(is24HourFormatUpdates) }
        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(is24HourFormatUpdates[0]).isFalse()

        val updatingReminder = mockReminders.find { it.id == mockId }!!.toReminderEditorUi(is24HourFormat = is24HourFormatUpdates[0])
        assertThat(updatingReminder.is24HourFormat).isFalse()

        val currentReminderEditor = reminderEditorViewModel.reminderEditorState.value.reminderEditor
        assertThat(currentReminderEditor).isEqualTo(updatingReminder)
        assertThat(currentReminderEditor.reminderEditorTime.hourFormat.hourFormat).isNotNull()

        testTimeFormatter.is24HourFormatChanged(update = true)

        assertThat(is24HourFormatUpdates[1]).isTrue()

        val updatedReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        val expectedReminderEditorState = updatedReminderEditorState.copy(reminderEditor = updatedReminderEditorState.reminderEditor.copy(is24HourFormat = true))
        assertThat(updatedReminderEditorState).isEqualTo(expectedReminderEditorState)
        assertThat(updatedReminderEditorState.reminderEditor.reminderEditorTime.hourFormat.hourFormat).isNull()
    }

    @Test
    fun initial_successful_title_validation_updates_ReminderEditorState() = runTest {
        val typedTitle = "Hello there !"

        savedStateHandle["id"] = null

        every { titleValidator.validate(any()) } returns ValidationStatus.Success
        assertThat(reminderEditorViewModel.titleValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        assertThat(initialReminderEditorState).isEqualTo(ReminderEditorState())

        assertThat(initialReminderEditorState.reminderEditor.title).isNull()

        reminderEditorViewModel.onAction(reminderEditorAction = ReminderEditorAction.OnTitleUpdate(title = typedTitle))
        assertThat(reminderEditorViewModel.titleValidationStatus).isEqualTo(ValidationStatus.Success)
        assertThat(reminderEditorViewModel.reminderEditorState.value.reminderEditor.title).isEqualTo(typedTitle)
        verify(exactly = 1) { titleValidator.validate(any()) }
    }

    @Test
    fun successful_title_validation_updates_ReminderEditorState() = runTest {
        val mockId: Long = 8
        savedStateHandle["id"] = mockId

        val updatingReminder = mockReminders.find { it.id == mockId }!!
        val updatingTitle = buildString { append("Updated ${updatingReminder.title}") }

        val expectedValidationStatus = ValidationStatus.Success
        every { titleValidator.validate(any()) } returns expectedValidationStatus
        assertThat(reminderEditorViewModel.titleValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialReminderEditorStateTitle = reminderEditorViewModel.reminderEditorState.value.reminderEditor.title
        assertThat(initialReminderEditorStateTitle).isEqualTo(updatingReminder.title)

        reminderEditorViewModel.onAction(reminderEditorAction = ReminderEditorAction.OnTitleUpdate(title = updatingTitle))
        assertThat(reminderEditorViewModel.titleValidationStatus).isEqualTo(expectedValidationStatus)
        assertThat(reminderEditorViewModel.reminderEditorState.value.reminderEditor.title).isEqualTo(updatingTitle)
        verify(exactly = 1) { titleValidator.validate(any()) }
    }

    @Test
    fun initial_error_title_validation_updates_ReminderEditorState() = runTest {
        val typedTitle = "1 hello there !"

        savedStateHandle["id"] = null

        val expectedValidationStatus = ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_DIGIT)
        every { titleValidator.validate(any()) } returns expectedValidationStatus
        assertThat(reminderEditorViewModel.titleValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        assertThat(initialReminderEditorState).isEqualTo(ReminderEditorState())

        assertThat(initialReminderEditorState.reminderEditor.title).isNull()

        reminderEditorViewModel.onAction(reminderEditorAction = ReminderEditorAction.OnTitleUpdate(title = typedTitle))
        assertThat(reminderEditorViewModel.titleValidationStatus).isEqualTo(expectedValidationStatus)
        assertThat(reminderEditorViewModel.reminderEditorState.value.reminderEditor.title).isEqualTo(typedTitle)
        verify(exactly = 1) { titleValidator.validate(any()) }
    }

    @Test
    fun error_title_validation_updates_ReminderEditorState() = runTest {
        val mockId: Long = 8
        savedStateHandle["id"] = mockId

        val updatingReminder = mockReminders.find { it.id == mockId }!!
        val updatingTitle = buildString { append("updated ${updatingReminder.title}") }

        val expectedValidationStatus = ValidationStatus.Error(error = AlarmValidationError.AlarmTitleValidation.STARTS_WITH_LOWERCASE)
        every { titleValidator.validate(any()) } returns expectedValidationStatus
        assertThat(reminderEditorViewModel.titleValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialReminderEditorStateTitle = reminderEditorViewModel.reminderEditorState.value.reminderEditor.title
        assertThat(initialReminderEditorStateTitle).isEqualTo(updatingReminder.title)

        reminderEditorViewModel.onAction(reminderEditorAction = ReminderEditorAction.OnTitleUpdate(title = updatingTitle))
        assertThat(reminderEditorViewModel.titleValidationStatus).isEqualTo(expectedValidationStatus)
        assertThat(reminderEditorViewModel.reminderEditorState.value.reminderEditor.title).isEqualTo(updatingTitle)
        verify(exactly = 1) { titleValidator.validate(any()) }
    }

    @Test
    fun initial_when_dateValidationStatus_is_successful_successful_time_validation_updates_ReminderEditorState() = runTest {
        val chosenTimestamp = System.currentTimeMillis() + 3600 * 1000
        every { timeValidator.validate(chosenTimestamp) } returns ValidationStatus.Success

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Unspecified)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Unspecified)
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.DATE))
            onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = chosenTimestamp))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.DATE)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Success)
        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.TIME))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = chosenTimestamp))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.TIME)
        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Success)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        val is24HourFormat = testTimeFormatter.is24HourFormat.first()

        val updatedReminderEditorTime = chosenTimestamp.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        val currentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        assertThat(currentReminderEditorTime).isEqualTo(updatedReminderEditorTime)
        verify(exactly = 2) { timeValidator.validate(chosenTimestamp) }
    }

    @Test
    fun when_dateValidationStatus_is_successful_successful_time_validation_updates_ReminderEditorState() = runTest {
        val mockId: Long = 4
        savedStateHandle["id"] = mockId

        val updatingReminder = mockReminders.find { it.id == mockId }!!
        val updatingReminderTimestamp = updatingReminder.reminderTime
        val chosenTimestamp = updatingReminderTimestamp + 3600 * 1000
        every { timeValidator.validate(chosenTimestamp) } returns ValidationStatus.Success

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Unspecified)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Unspecified)
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.DATE))
            onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = chosenTimestamp))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.DATE)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Success)
        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.TIME))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = chosenTimestamp))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.TIME)
        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Success)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        val is24HourFormat = testTimeFormatter.is24HourFormat.first()

        val updatedReminderEditorTime = chosenTimestamp.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        val currentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        assertThat(currentReminderEditorTime.value).isGreaterThan(updatingReminderTimestamp)
        assertThat(currentReminderEditorTime).isEqualTo(updatedReminderEditorTime)
        verify(exactly = 2) { timeValidator.validate(chosenTimestamp) }
    }

    @Test
    fun initial_when_dateValidationStatus_is_successful_error_time_validation_updates_ReminderEditorState() = runTest {
        val chosenTimestampDate = System.currentTimeMillis() + 3600 * 1000
        val chosenTimestampTime = System.currentTimeMillis() - 3600 * 1000

        val expectedValidationStatusTime = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TENSE)
        every { timeValidator.validate(any()) } returnsMany listOf(ValidationStatus.Success, expectedValidationStatusTime)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Unspecified)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Unspecified)
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.DATE))
            onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = chosenTimestampDate))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.DATE)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Success)
        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.TIME))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = chosenTimestampTime))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.TIME)
        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(expectedValidationStatusTime)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        val is24HourFormat = testTimeFormatter.is24HourFormat.first()

        val updatedReminderEditorTime = chosenTimestampTime.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        val currentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        assertThat(currentReminderEditorTime).isEqualTo(updatedReminderEditorTime)
        verify(exactly = 2) { timeValidator.validate(any()) }
    }

    @Test
    fun when_dateValidationStatus_is_successful_error_time_validation_updates_ReminderEditorState() = runTest {
        val mockId: Long = 4
        savedStateHandle["id"] = mockId

        val updatingReminder = mockReminders.find { it.id == mockId }!!
        val updatingReminderTimestamp = updatingReminder.reminderTime
        val chosenTimestampDate = updatingReminderTimestamp + 3600 * 1000
        val chosenTimestampTime = System.currentTimeMillis() - 3600 * 1000

        val expectedValidationStatusTime = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TENSE)
        every { timeValidator.validate(any()) } returnsMany listOf(ValidationStatus.Success, expectedValidationStatusTime)

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Unspecified)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Unspecified)
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.DATE))
            onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = chosenTimestampDate))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.DATE)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Success)
        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.TIME))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = chosenTimestampTime))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.TIME)
        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(expectedValidationStatusTime)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        val is24HourFormat = testTimeFormatter.is24HourFormat.first()

        val updatedReminderEditorTime = chosenTimestampTime.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        val currentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        assertThat(currentReminderEditorTime.value).isLessThan(updatingReminderTimestamp)
        assertThat(currentReminderEditorTime).isEqualTo(updatedReminderEditorTime)
        verify(exactly = 2) { timeValidator.validate(any()) }
    }

    @Test
    fun initial_chosen_time_updates_ReminderEditorState() = runTest {
        val chosenTimestamp = System.currentTimeMillis()
        val unspecifiedValidationStatus = ValidationStatus.Unspecified
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(unspecifiedValidationStatus)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(unspecifiedValidationStatus)
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.TIME))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = chosenTimestamp))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.TIME)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        val is24HourFormat = testTimeFormatter.is24HourFormat.first()

        val updatedReminderEditorTime = chosenTimestamp.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        val currentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        assertThat(currentReminderEditorTime).isEqualTo(updatedReminderEditorTime)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(unspecifiedValidationStatus)
    }

    @Test
    fun initial_chosen_time_after_changed_is24HourFormat_changes_timeHourFormat() = runTest {
        val chosenTimestamp = System.currentTimeMillis()
        val unspecifiedValidationStatus = ValidationStatus.Unspecified
        val is24HourFormatUpdates = mutableListOf<Boolean>()
        backgroundScope.launch(coroutineDispatcher) { testTimeFormatter.is24HourFormat.toList(is24HourFormatUpdates) }
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(unspecifiedValidationStatus)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(unspecifiedValidationStatus)
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.TIME))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = chosenTimestamp))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.TIME)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        val is24HourFormat = is24HourFormatUpdates[0]
        assertThat(is24HourFormat).isTrue()

        val updatedReminderEditorTime = chosenTimestamp.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        val currentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        val hourFormat = currentReminderEditorTime.hourFormat.hourFormat
        assertThat(hourFormat).isNull()
        assertThat(currentReminderEditorTime).isEqualTo(updatedReminderEditorTime)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(unspecifiedValidationStatus)

        testTimeFormatter.is24HourFormatChanged(update = false)
        val updatedIs24HourFormat = is24HourFormatUpdates[1]
        assertThat(updatedIs24HourFormat).isFalse()

        val finalReminderEditorTime = chosenTimestamp.toDisplayableReminderEditorTime(is24HourFormat = updatedIs24HourFormat)
        val updatedCurrentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        val updatedHourFormat = updatedCurrentReminderEditorTime.hourFormat.hourFormat
        assertThat(updatedHourFormat).isNotNull()
        assertThat(updatedHourFormat!!.name).isEqualTo(updatedCurrentReminderEditorTime.hourFormat.value)
        assertThat(updatedCurrentReminderEditorTime).isEqualTo(finalReminderEditorTime)
    }

    @Test
    fun chosen_time_after_changed_is24HourFormat_changes_timeHourFormat() = runTest {
        val mockId: Long = 4
        savedStateHandle["id"] = mockId

        val updatingReminder = mockReminders.find { it.id == mockId }!!

        val chosenTimestamp = updatingReminder.reminderTime - 3600 * 1000
        val unspecifiedValidationStatus = ValidationStatus.Unspecified
        val is24HourFormatUpdates = mutableListOf<Boolean>()

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        testTimeFormatter.is24HourFormatChanged(update = false)

        backgroundScope.launch(coroutineDispatcher) { testTimeFormatter.is24HourFormat.toList(is24HourFormatUpdates) }
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(reminderEditorViewModel.timeValidationStatus).isEqualTo(unspecifiedValidationStatus)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(unspecifiedValidationStatus)
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.TIME))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = chosenTimestamp))
        }
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isEqualTo(PickerDialog.TIME)

        reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
        assertThat(reminderEditorViewModel.reminderEditorState.value.pickerDialog).isNull()

        val is24HourFormat = is24HourFormatUpdates[0]
        assertThat(is24HourFormat).isFalse()

        val updatedReminderEditorTime = chosenTimestamp.toDisplayableReminderEditorTime(is24HourFormat = is24HourFormat)
        val currentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        val hourFormat = currentReminderEditorTime.hourFormat.hourFormat
        assertThat(hourFormat).isNotNull()
        assertThat(hourFormat!!.name).isEqualTo(currentReminderEditorTime.hourFormat.value)
        assertThat(currentReminderEditorTime.value).isLessThan(updatingReminder.reminderTime)
        assertThat(currentReminderEditorTime).isEqualTo(updatedReminderEditorTime)
        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(unspecifiedValidationStatus)

        testTimeFormatter.is24HourFormatChanged(update = true)
        val updatedIs24HourFormat = is24HourFormatUpdates[1]
        assertThat(updatedIs24HourFormat).isTrue()

        val finalReminderEditorTime = chosenTimestamp.toDisplayableReminderEditorTime(is24HourFormat = updatedIs24HourFormat)
        val updatedCurrentReminderEditorTime = reminderEditorViewModel.reminderEditorState.value.reminderEditor.reminderEditorTime
        val updatedHourFormat = updatedCurrentReminderEditorTime.hourFormat.hourFormat
        assertThat(updatedHourFormat).isNull()
        assertThat(updatedCurrentReminderEditorTime.hourFormat.value).isNull()
        assertThat(updatedCurrentReminderEditorTime).isEqualTo(finalReminderEditorTime)
    }

    @Test
    fun initial_successful_date_validation_updates_ReminderEditorState() = runTest {
        val chosenTimestampDate = System.currentTimeMillis() + 3600 * 1000
        val expectedDateValidationResult = ValidationStatus.Success
        every { timeValidator.validate(chosenTimestampDate) } returns expectedDateValidationResult

        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        reminderEditorViewModel.reminderEditorState.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(ReminderEditorState())

            reminderEditorViewModel.apply {
                onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.DATE))
                onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = chosenTimestampDate))
            }
            val updatedReminderEditorState = expectMostRecentItem()
            assertThat(updatedReminderEditorState.pickerDialog).isEqualTo(PickerDialog.DATE)
            assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(expectedDateValidationResult)

            reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
            val currentReminderEditorState = awaitItem()
            assertThat(currentReminderEditorState.pickerDialog).isNull()

            val updatedReminderEditorDate = chosenTimestampDate.toDisplayableReminderEditorDate()
            assertThat(currentReminderEditorState.reminderEditor.reminderEditorDate).isEqualTo(updatedReminderEditorDate)
        }
        verify(exactly = 1) { timeValidator.validate(chosenTimestampDate) }
    }

    @Test
    fun successful_date_validation_updates_ReminderEditorState() = runTest {
        val mockId: Long = 7
        savedStateHandle["id"] = mockId

        val updatingReminder = mockReminders.find { it.id == mockId }!!
        val updatingReminderTimestamp = updatingReminder.reminderTime

        val chosenTimestampDate = updatingReminderTimestamp + 3600 * 1000
        val expectedDateValidationResult = ValidationStatus.Success
        every { timeValidator.validate(chosenTimestampDate) } returns expectedDateValidationResult

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        reminderEditorViewModel.reminderEditorState.test {
            val is24HourFormat = testTimeFormatter.is24HourFormat.first()
            val retrievedReminderEditorState = ReminderEditorState(reminderEditor = updatingReminder.toReminderEditorUi(is24HourFormat = is24HourFormat))
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(retrievedReminderEditorState)

            reminderEditorViewModel.apply {
                onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.DATE))
                onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = chosenTimestampDate))
            }
            val updatedReminderEditorState = expectMostRecentItem()
            assertThat(updatedReminderEditorState.pickerDialog).isEqualTo(PickerDialog.DATE)
            assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(expectedDateValidationResult)

            reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
            val currentReminderEditorState = awaitItem()
            assertThat(currentReminderEditorState.pickerDialog).isNull()

            val updatedReminderEditorDate = chosenTimestampDate.toDisplayableReminderEditorDate()
            val currentReminderEditorDate = currentReminderEditorState.reminderEditor.reminderEditorDate
            assertThat(currentReminderEditorDate).isNotEqualTo(retrievedReminderEditorState.reminderEditor.reminderEditorDate)
            assertThat(currentReminderEditorDate.value).isGreaterThan(retrievedReminderEditorState.reminderEditor.reminderEditorDate.value)
            assertThat(currentReminderEditorDate).isEqualTo(updatedReminderEditorDate)
        }
        verify(exactly = 1) { timeValidator.validate(chosenTimestampDate) }
    }

    @Test
    fun initial_error_date_validation_updates_ReminderEditorState() = runTest {
        val chosenTimestampDate = System.currentTimeMillis() - 3600 * 1000
        val expectedDateValidationResult = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TENSE)
        every { timeValidator.validate(chosenTimestampDate) } returns expectedDateValidationResult

        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        reminderEditorViewModel.reminderEditorState.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(ReminderEditorState())

            reminderEditorViewModel.apply {
                onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.DATE))
                onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = chosenTimestampDate))
            }
            val updatedReminderEditorState = expectMostRecentItem()
            assertThat(updatedReminderEditorState.pickerDialog).isEqualTo(PickerDialog.DATE)
            assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(expectedDateValidationResult)

            reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
            val currentReminderEditorState = awaitItem()
            assertThat(currentReminderEditorState.pickerDialog).isNull()

            val updatedReminderEditorDate = chosenTimestampDate.toDisplayableReminderEditorDate()
            val currentReminderEditorDate = currentReminderEditorState.reminderEditor.reminderEditorDate
            assertThat(currentReminderEditorDate).isEqualTo(updatedReminderEditorDate)
        }
        verify(exactly = 1) { timeValidator.validate(chosenTimestampDate) }
    }

    @Test
    fun error_date_validation_updates_ReminderEditorState() = runTest {
        val mockId: Long = 7
        savedStateHandle["id"] = mockId

        val updatingReminder = mockReminders.find { it.id == mockId }!!
        val updatingReminderTimestamp = updatingReminder.reminderTime

        val chosenTimestampDate = updatingReminderTimestamp - 3600 * 1000
        val expectedDateValidationResult = ValidationStatus.Error(error = AlarmValidationError.AlarmReminderTimeValidation.PAST_TENSE)
        every { timeValidator.validate(chosenTimestampDate) } returns expectedDateValidationResult

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(ValidationStatus.Unspecified)

        reminderEditorViewModel.reminderEditorState.test {
            val is24HourFormat = testTimeFormatter.is24HourFormat.first()
            val retrievedReminderEditorState = ReminderEditorState(reminderEditor = updatingReminder.toReminderEditorUi(is24HourFormat = is24HourFormat))
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(retrievedReminderEditorState)
            assertThat(initialState.pickerDialog).isNull()

            reminderEditorViewModel.apply {
                onAction(reminderEditorAction = ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = PickerDialog.DATE))
                onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = chosenTimestampDate))
            }
            val updatedReminderEditorState = expectMostRecentItem()
            assertThat(updatedReminderEditorState.pickerDialog).isEqualTo(PickerDialog.DATE)
            assertThat(reminderEditorViewModel.dateValidationStatus).isEqualTo(expectedDateValidationResult)

            reminderEditorViewModel.onAction(ReminderEditorAction.OnPickerDialogVisibilityUpdate(pickerDialog = null))
            val currentReminderEditorState = awaitItem()
            assertThat(currentReminderEditorState.pickerDialog).isNull()

            val updatedReminderEditorDate = chosenTimestampDate.toDisplayableReminderEditorDate()
            val currentReminderEditorDate = currentReminderEditorState.reminderEditor.reminderEditorDate
            assertThat(currentReminderEditorDate).isNotEqualTo(retrievedReminderEditorState.reminderEditor.reminderEditorDate)
            assertThat(currentReminderEditorDate.value).isLessThan(retrievedReminderEditorState.reminderEditor.reminderEditorDate.value)
            assertThat(currentReminderEditorDate).isEqualTo(updatedReminderEditorDate)
        }
        verify(exactly = 1) { timeValidator.validate(chosenTimestampDate) }
    }

    @Test
    fun retrieve_id_load_reminder_and_update_periodicity() = runTest {
        val mockId: Long =  4
        savedStateHandle["id"] = mockId

        val is24HourFormat = testTimeFormatter.is24HourFormat.first()
        val expectedReminder = mockReminders.find { it.id == mockId }!!.toReminderEditorUi(is24HourFormat = is24HourFormat)

        val initialPeriodicity = Periodicity.ONCE
        val defaultPeriodicity = reminderEditorViewModel.reminderEditorState.value.reminderEditor.periodicity
        assertThat(defaultPeriodicity).isEqualTo(initialPeriodicity)

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val updatedReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        assertThat(updatedReminderEditorState.reminderEditor.periodicity).isEqualTo(expectedReminder.periodicity)
    }

    @Test
    fun retrieve_id_load_reminder_and_change_periodicity() = runTest {
        val mockId: Long =  4
        savedStateHandle["id"] = mockId

        val expectedPeriodicity = Periodicity.DAILY

        val is24HourFormat = testTimeFormatter.is24HourFormat.first()
        val expectedReminder = mockReminders.find { it.id == mockId }!!.toReminderEditorUi(is24HourFormat = is24HourFormat)

        val initialPeriodicity = Periodicity.ONCE
        val defaultPeriodicity = reminderEditorViewModel.reminderEditorState.value.reminderEditor.periodicity
        assertThat(defaultPeriodicity).isEqualTo(initialPeriodicity)

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val firstReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        assertThat(firstReminderEditorState.reminderEditor.periodicity).isEqualTo(expectedReminder.periodicity)
        assertThat(firstReminderEditorState.isPeriodicityDropdownMenuExpanded).isFalse()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnPeriodicityDropdownMenuVisibilityUpdate)
            onAction(reminderEditorAction = ReminderEditorAction.OnPeriodicityUpdate(periodicity = expectedPeriodicity))
        }

        val secondReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        assertThat(secondReminderEditorState.isPeriodicityDropdownMenuExpanded).isTrue()
        assertThat(secondReminderEditorState.reminderEditor.periodicity).isEqualTo(expectedPeriodicity)

        reminderEditorViewModel.onAction(reminderEditorAction = ReminderEditorAction.OnPeriodicityDropdownMenuVisibilityUpdate)

        val finalReminderEditorState = reminderEditorViewModel.reminderEditorState.value
        assertThat(finalReminderEditorState.isPeriodicityDropdownMenuExpanded).isFalse()
    }

    @Test
    fun successfully_insert_a_new_reminder_send_result_to_channel() = runTest {
        val insertedReminder = Reminder(
            id = 0,
            title = "Do not be lazy !",
            reminderTime = System.currentTimeMillis() + 3600 * 1000,
            periodicity = Periodicity.WEEKDAYS
        )
        val eventValues = mutableListOf<Event<ReminderEditorOneTimeEvent, ReminderError>>()

        savedStateHandle["id"] = null

        every { titleValidator.validate(insertedReminder.title) } returns ValidationStatus.Success
        every { timeValidator.validate(insertedReminder.reminderTime) } returnsMany listOf(ValidationStatus.Success, ValidationStatus.Success)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.eventFlow.toList(eventValues) }
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialState = reminderEditorViewModel.reminderEditorState.value
        assertThat(initialState).isEqualTo(ReminderEditorState())

        assertThat(eventValues).isEmpty()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnTitleUpdate(title = insertedReminder.title))
            onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = insertedReminder.reminderTime))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = insertedReminder.reminderTime))
            onAction(reminderEditorAction = ReminderEditorAction.OnPeriodicityUpdate(periodicity = insertedReminder.periodicity))
            onAction(reminderEditorAction = ReminderEditorAction.OnUpsertReminder)
        }
        val finalState = reminderEditorViewModel.reminderEditorState.value.reminderEditor
        val is24HourFormat = finalState.is24HourFormat
        val expectedInsertedReminder = insertedReminder.toReminderEditorUi(is24HourFormat = is24HourFormat)
        assertThat(finalState.title).isEqualTo(expectedInsertedReminder.title)
        assertThat(finalState.reminderEditorTime).isEqualTo(expectedInsertedReminder.reminderEditorTime)
        assertThat(finalState.reminderEditorDate).isEqualTo(expectedInsertedReminder.reminderEditorDate)
        assertThat(finalState.is24HourFormat).isEqualTo(expectedInsertedReminder.is24HourFormat)
        assertThat(finalState.periodicity).isEqualTo(expectedInsertedReminder.periodicity)

        val eventFlow = eventValues[0]
        assertThat(eventFlow.isSuccess).isTrue()

        val reminderEditorOneTimeEvent = eventFlow.getData
        assertThat(reminderEditorOneTimeEvent).isInstanceOf(ReminderEditorOneTimeEvent.OnUpsertClick::class.java)

        val onUpsertClickText = (reminderEditorOneTimeEvent as ReminderEditorOneTimeEvent.OnUpsertClick).uiText.asString(context = context)
        val expectedText = context.getString(string.reminderSuccessfullyAdded)
        assertThat(onUpsertClickText).isEqualTo(expectedText)

        verify(exactly = 1) { titleValidator.validate(insertedReminder.title) }
        verify(exactly = 2) { timeValidator.validate(insertedReminder.reminderTime) }
    }

    @Test
    fun retrieve_id_load_reminder_successfully_update_reminder_send_result_to_channel() = runTest {
        val initialReminder = mockReminders[8]
        val updatedReminder = initialReminder.copy(
            title = "Do not be lazy !",
            reminderTime = initialReminder.reminderTime + 3600 * 1000,
            periodicity = Periodicity.DAILY
        )
        val eventValues = mutableListOf<Event<ReminderEditorOneTimeEvent, ReminderError>>()

        savedStateHandle["id"] = initialReminder.id

        every { titleValidator.validate(updatedReminder.title) } returns ValidationStatus.Success
        every { timeValidator.validate(updatedReminder.reminderTime) } returnsMany listOf(ValidationStatus.Success, ValidationStatus.Success)

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.eventFlow.toList(eventValues) }
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialState = reminderEditorViewModel.reminderEditorState.value
        assertThat(initialState.reminderEditor).isEqualTo(initialReminder.toReminderEditorUi(is24HourFormat = initialState.reminderEditor.is24HourFormat))

        assertThat(eventValues).isEmpty()

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnTitleUpdate(title = updatedReminder.title))
            onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = updatedReminder.reminderTime))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = updatedReminder.reminderTime))
            onAction(reminderEditorAction = ReminderEditorAction.OnPeriodicityUpdate(periodicity = updatedReminder.periodicity))
            onAction(reminderEditorAction = ReminderEditorAction.OnUpsertReminder)
        }
        val finalState = reminderEditorViewModel.reminderEditorState.value.reminderEditor
        val is24HourFormat = finalState.is24HourFormat
        val expectedUpdatedReminder = updatedReminder.toReminderEditorUi(is24HourFormat = is24HourFormat)
        assertThat(finalState.title).isEqualTo(expectedUpdatedReminder.title)
        assertThat(finalState.reminderEditorTime).isEqualTo(expectedUpdatedReminder.reminderEditorTime)
        assertThat(finalState.reminderEditorDate).isEqualTo(expectedUpdatedReminder.reminderEditorDate)
        assertThat(finalState.is24HourFormat).isEqualTo(expectedUpdatedReminder.is24HourFormat)
        assertThat(finalState.periodicity).isEqualTo(expectedUpdatedReminder.periodicity)

        val eventFlow = eventValues[0]
        assertThat(eventFlow.isSuccess).isTrue()

        val reminderEditorOneTimeEvent = eventFlow.getData
        assertThat(reminderEditorOneTimeEvent).isInstanceOf(ReminderEditorOneTimeEvent.OnUpsertClick::class.java)

        val onUpsertClickText = (reminderEditorOneTimeEvent as ReminderEditorOneTimeEvent.OnUpsertClick).uiText.asString(context = context)
        val expectedText = context.getString(string.reminderSuccessfullyUpdated)
        assertThat(onUpsertClickText).isEqualTo(expectedText)

        verify(exactly = 1) { titleValidator.validate(updatedReminder.title) }
        verify(exactly = 2) { timeValidator.validate(updatedReminder.reminderTime) }
    }

    @Test
    fun unsuccessfully_insert_a_new_reminder_send_result_to_channel() = runTest {
        val insertedReminder = Reminder(
            id = 0,
            title = "Do not be lazy !",
            reminderTime = System.currentTimeMillis() + 3600 * 1000,
            periodicity = Periodicity.WEEKDAYS
        )
        val eventValues = mutableListOf<Event<ReminderEditorOneTimeEvent, ReminderError>>()

        val expectedError = ReminderError.Database(error = DatabaseError.SQLITE_EXCEPTION)

        savedStateHandle["id"] = null

        every { titleValidator.validate(insertedReminder.title) } returns ValidationStatus.Success
        every { timeValidator.validate(insertedReminder.reminderTime) } returnsMany listOf(ValidationStatus.Success, ValidationStatus.Success)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.eventFlow.toList(eventValues) }
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialState = reminderEditorViewModel.reminderEditorState.value
        assertThat(initialState).isEqualTo(ReminderEditorState())

        assertThat(eventValues).isEmpty()

        testReminderEditorRepository.updateError(error = expectedError)

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnTitleUpdate(title = insertedReminder.title))
            onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = insertedReminder.reminderTime))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = insertedReminder.reminderTime))
            onAction(reminderEditorAction = ReminderEditorAction.OnPeriodicityUpdate(periodicity = insertedReminder.periodicity))
            onAction(reminderEditorAction = ReminderEditorAction.OnUpsertReminder)
        }
        val finalState = reminderEditorViewModel.reminderEditorState.value.reminderEditor
        val is24HourFormat = finalState.is24HourFormat
        val expectedInsertedReminder = insertedReminder.toReminderEditorUi(is24HourFormat = is24HourFormat)
        assertThat(finalState.title).isEqualTo(expectedInsertedReminder.title)
        assertThat(finalState.reminderEditorTime).isEqualTo(expectedInsertedReminder.reminderEditorTime)
        assertThat(finalState.reminderEditorDate).isEqualTo(expectedInsertedReminder.reminderEditorDate)
        assertThat(finalState.is24HourFormat).isEqualTo(expectedInsertedReminder.is24HourFormat)
        assertThat(finalState.periodicity).isEqualTo(expectedInsertedReminder.periodicity)

        val eventFlow = eventValues[0]
        assertThat(eventFlow.isError).isTrue()

        val reminderEditorErrorEvent = eventFlow.getError
        assertThat(reminderEditorErrorEvent).isInstanceOf(ReminderError.Database::class.java)

        val reminderErrorText = reminderEditorErrorEvent.toString(context = context)
        val expectedErrorText = context.getString(string.sqliteException)
        assertThat(reminderErrorText).isEqualTo(expectedErrorText)

        verify(exactly = 1) { titleValidator.validate(insertedReminder.title) }
        verify(exactly = 2) { timeValidator.validate(insertedReminder.reminderTime) }
    }

    @Test
    fun retrieve_id_load_reminder_successfully_update_reminder_unsuccessfully_send_result_to_channel() = runTest {
        val initialReminder = mockReminders[8]
        val updatedReminder = initialReminder.copy(
            title = "Do not be lazy !",
            reminderTime = initialReminder.reminderTime + 3600 * 1000,
            periodicity = Periodicity.DAILY
        )
        val eventValues = mutableListOf<Event<ReminderEditorOneTimeEvent, ReminderError>>()

        val expectedError = ReminderError.Alarm(error = AlarmError.CANCELED)

        savedStateHandle["id"] = initialReminder.id

        every { titleValidator.validate(updatedReminder.title) } returns ValidationStatus.Success
        every { timeValidator.validate(updatedReminder.reminderTime) } returnsMany listOf(ValidationStatus.Success, ValidationStatus.Success)

        testReminderEditorRepository.setReminders(reminders = mockReminders)

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.eventFlow.toList(eventValues) }
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        val initialState = reminderEditorViewModel.reminderEditorState.value
        assertThat(initialState.reminderEditor).isEqualTo(initialReminder.toReminderEditorUi(is24HourFormat = initialState.reminderEditor.is24HourFormat))

        assertThat(eventValues).isEmpty()

        testReminderEditorRepository.updateError(error = expectedError)

        reminderEditorViewModel.apply {
            onAction(reminderEditorAction = ReminderEditorAction.OnTitleUpdate(title = updatedReminder.title))
            onAction(reminderEditorAction = ReminderEditorAction.OnDateUpdate(date = updatedReminder.reminderTime))
            onAction(reminderEditorAction = ReminderEditorAction.OnTimeUpdate(time = updatedReminder.reminderTime))
            onAction(reminderEditorAction = ReminderEditorAction.OnPeriodicityUpdate(periodicity = updatedReminder.periodicity))
            onAction(reminderEditorAction = ReminderEditorAction.OnUpsertReminder)
        }
        val finalState = reminderEditorViewModel.reminderEditorState.value.reminderEditor
        val is24HourFormat = finalState.is24HourFormat
        val expectedUpdatedReminder = updatedReminder.toReminderEditorUi(is24HourFormat = is24HourFormat)
        assertThat(finalState.title).isEqualTo(expectedUpdatedReminder.title)
        assertThat(finalState.reminderEditorTime).isEqualTo(expectedUpdatedReminder.reminderEditorTime)
        assertThat(finalState.reminderEditorDate).isEqualTo(expectedUpdatedReminder.reminderEditorDate)
        assertThat(finalState.is24HourFormat).isEqualTo(expectedUpdatedReminder.is24HourFormat)
        assertThat(finalState.periodicity).isEqualTo(expectedUpdatedReminder.periodicity)

        val eventFlow = eventValues[0]
        assertThat(eventFlow.isError).isTrue()

        val reminderEditorErrorEvent = eventFlow.getError
        assertThat(reminderEditorErrorEvent).isInstanceOf(ReminderError.Alarm::class.java)

        val reminderErrorText = reminderEditorErrorEvent.toString(context = context)
        val expectedErrorText = context.getString(string.alarmCanceledException)
        assertThat(reminderErrorText).isEqualTo(expectedErrorText)

        verify(exactly = 1) { titleValidator.validate(updatedReminder.title) }
        verify(exactly = 2) { timeValidator.validate(updatedReminder.reminderTime) }
    }

    @Test
    fun on_back_click_send_event_to_channel() = runTest {
        savedStateHandle["id"] = null
        val eventValues = mutableListOf<Event<ReminderEditorOneTimeEvent, ReminderError>>()

        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.eventFlow.toList(eventValues) }
        backgroundScope.launch(coroutineDispatcher) { reminderEditorViewModel.reminderEditorState.collect() }

        assertThat(eventValues).isEmpty()

        reminderEditorViewModel.onAction(reminderEditorAction = ReminderEditorAction.OnBackClick)

        val eventFlow = eventValues[0]
        assertThat(eventFlow.isSuccess).isTrue()

        val reminderOneTimeEvent = eventFlow.getData
        assertThat(reminderOneTimeEvent).isInstanceOf(ReminderEditorOneTimeEvent.OnBackClick::class.java)
    }
}