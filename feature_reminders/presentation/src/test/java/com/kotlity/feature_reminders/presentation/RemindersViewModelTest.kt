package com.kotlity.feature_reminders.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.domain.Periodicity
import com.kotlity.core.domain.Reminder
import com.kotlity.core.domain.util.AlarmError
import com.kotlity.core.domain.util.DatabaseError
import com.kotlity.core.domain.util.ReminderError
import com.kotlity.core.domain.util.Result
import com.kotlity.core.presentation.util.Event
import com.kotlity.feature_reminders.domain.RemindersRepository
import com.kotlity.feature_reminders.presentation.actions.RemindersAction
import com.kotlity.feature_reminders.presentation.events.ReminderOneTimeEvent
import com.kotlity.feature_reminders.presentation.mappers.toReminderUi
import com.kotlity.feature_reminders.presentation.states.SelectedReminderState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RemindersViewModelTest {

    @MockK
    private lateinit var remindersRepository: RemindersRepository

    private lateinit var remindersViewModel: RemindersViewModel

    @get:Rule
    val mockKRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        remindersViewModel = RemindersViewModel(remindersRepository)
    }

    @Test
    fun `on initial call onLoadReminders set state to loading equals true`() = runTest {
        every { remindersRepository.getAllReminders() } returns flowOf(Result.Loading)
        val initialState = remindersViewModel.state.value
        assertThat(initialState.isLoading).isFalse()
        remindersViewModel.state.test {
            val isLoading = awaitItem().isLoading
            assertThat(isLoading).isTrue()
        }
        verify(exactly = 1) { remindersRepository.getAllReminders() }
    }

    @Test
    fun `onLoadReminders returns reminders and updates state with data`() = runTest {
        val reminders = (0..5).map {
            Reminder(
                id = it.toLong(),
                title = "title$it",
                reminderTime = 1734127200000 + it.toLong() * 1000,
                periodicity = if (it % 2 == 0) Periodicity.WEEKDAYS else Periodicity.ONCE
            )
        }
        every { remindersRepository.getAllReminders() } returns flowOf(Result.Success(reminders))
        val initialState = remindersViewModel.state.value
        assertThat(initialState.reminders).isEmpty()
        remindersViewModel.state.test {
            val updatedState = awaitItem()
            assertThat(updatedState.isLoading).isFalse()
            assertThat(updatedState.reminders).isEqualTo(reminders.map { it.toReminderUi() })
        }
        verify(exactly = 1) { remindersRepository.getAllReminders() }
    }

    @Test
    fun `onLoadReminders returns DatabaseError dot SQLiteException and send it to the eventChannel`() = runTest {
        coEvery { remindersRepository.getAllReminders() } returns flowOf(Result.Error(DatabaseError.SQLITE_EXCEPTION))
        remindersViewModel.state.test {
            val updatedState = awaitItem()
            assertThat(updatedState.isLoading).isFalse()
            assertThat(updatedState.reminders).isEmpty()
        }
        remindersViewModel.eventFlow.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.Error::class.java)
            val reminderError = (event as Event.Error<ReminderError>).error
            assertThat(reminderError).isInstanceOf(ReminderError.Database::class.java)
            val databaseError = (reminderError as ReminderError.Database).error
            assertThat(databaseError).isEqualTo(DatabaseError.SQLITE_EXCEPTION)
        }
        verify(exactly = 1) { remindersRepository.getAllReminders() }
    }

    @Test
    fun `onReminderDelete returns successful result and send ReminderOneTimeEvent dot Delete to the eventChannel`() = runTest {
        coEvery { remindersRepository.deleteReminder(any()) } returns Result.Success(Unit)
        remindersViewModel.onAction(RemindersAction.OnReminderDelete(10))
        remindersViewModel.eventFlow.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.Success::class.java)
            val reminderOneTimeEvent = (event as Event.Success).data
            assertThat(reminderOneTimeEvent).isInstanceOf(ReminderOneTimeEvent.Delete::class.java)
        }
        coVerify(exactly = 1) { remindersRepository.deleteReminder(any()) }
    }

    @Test
    fun `onReminderDelete returns DatabaseError dot IllegalArgument and send it to the eventChannel`() = runTest {
        coEvery { remindersRepository.deleteReminder(any()) } returns Result.Error(ReminderError.Database(DatabaseError.ILLEGAL_ARGUMENT))
        remindersViewModel.onAction(RemindersAction.OnReminderDelete(1000))
        remindersViewModel.eventFlow.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.Error::class.java)
            val reminderErrorEvent = (event as Event.Error).error
            assertThat(reminderErrorEvent).isInstanceOf(ReminderError.Database::class.java)
            val databaseError = (reminderErrorEvent as ReminderError.Database).error
            assertThat(databaseError).isEqualTo(DatabaseError.ILLEGAL_ARGUMENT)
        }
        coVerify(exactly = 1) { remindersRepository.deleteReminder(any()) }
    }

    @Test
    fun `onReminderDelete returns AlarmError dot Security and send it to the eventChannel`() = runTest {
        coEvery { remindersRepository.deleteReminder(any()) } returns Result.Error(ReminderError.Alarm(AlarmError.SECURITY))
        remindersViewModel.onAction(RemindersAction.OnReminderDelete(5))
        remindersViewModel.eventFlow.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.Error::class.java)
            val reminderErrorEvent = (event as Event.Error).error
            assertThat(reminderErrorEvent).isInstanceOf(ReminderError.Alarm::class.java)
            val alarmError = (reminderErrorEvent as ReminderError.Alarm).error
            assertThat(alarmError).isEqualTo(AlarmError.SECURITY)
        }
        coVerify(exactly = 1) { remindersRepository.deleteReminder(any()) }
    }

    @Test
    fun `onReminderAdd sends ReminderOneTimeEvent dot Add to the eventChannel`() = runTest {
        remindersViewModel.onAction(RemindersAction.OnReminderAdd)
        remindersViewModel.eventFlow.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.Success::class.java)
            val reminderOneTimeEvent = (event as Event.Success).data
            assertThat(reminderOneTimeEvent).isInstanceOf(ReminderOneTimeEvent.Add::class.java)
        }
    }

    @Test
    fun `onReminderEdit sends ReminderOneTimeEvent dot Edit with reminderId to the eventChannel`() = runTest {
        remindersViewModel.onAction(RemindersAction.OnReminderEdit(3))
        remindersViewModel.eventFlow.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(Event.Success::class.java)
            val reminderOneTimeEvent = (event as Event.Success).data
            assertThat(reminderOneTimeEvent).isInstanceOf(ReminderOneTimeEvent.Edit::class.java)
            val reminderIdToEdit = (reminderOneTimeEvent as ReminderOneTimeEvent.Edit).id
            assertThat(reminderIdToEdit).isEqualTo(3)
        }
    }

    @Test
    fun `onReminderSelected updates selectedReminderId and x and y coordinates with passed id and coordinates`() = runTest {
        every { remindersRepository.getAllReminders() } returns emptyFlow()
        remindersViewModel.state.test {
            val initialSelectedReminderState = awaitItem().selectedReminderState
            assertThat(initialSelectedReminderState).isEqualTo(SelectedReminderState())
            remindersViewModel.onAction(RemindersAction.OnReminderSelect(id = 1, xPosition = 80, yPosition = 125))
            val updatedSelectedReminderState = awaitItem().selectedReminderState
            val expectedSelectedReminderState = SelectedReminderState(id = 1, xPosition = 80, yPosition = 125)
            assertThat(updatedSelectedReminderState).isEqualTo(expectedSelectedReminderState)
        }
    }

    @Test
    fun `onReminderUnselected sets selectedReminderId and x and y coordinates to null`() = runTest {
        every { remindersRepository.getAllReminders() } returns emptyFlow()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            remindersViewModel.state.collect { }
        }
        val initialSelectedReminderState = remindersViewModel.state.value.selectedReminderState
        assertThat(initialSelectedReminderState).isEqualTo(SelectedReminderState())
        remindersViewModel.onAction(RemindersAction.OnReminderSelect(id = 2, xPosition = 80, yPosition = 270))
        val updatedSelectedReminderState = remindersViewModel.state.value.selectedReminderState
        val expectedUpdatedSelectedReminderState = SelectedReminderState(id = 2, xPosition = 80, yPosition = 270)
        assertThat(updatedSelectedReminderState).isEqualTo(expectedUpdatedSelectedReminderState)
        remindersViewModel.onAction(RemindersAction.OnReminderUnselect)
        val nullableSelectedReminderState = remindersViewModel.state.value.selectedReminderState
        assertThat(nullableSelectedReminderState).isEqualTo(initialSelectedReminderState)
    }

    @Test
    fun `onIsAlertDialogRationaleVisibleUpdate sets isAlertDialogRationaleVisible to true`() = runTest {
        every { remindersRepository.getAllReminders() } returns emptyFlow()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            remindersViewModel.state.collect { }
        }
        val initialIsAlertDialogRationaleVisible = remindersViewModel.state.value.isAlertDialogRationaleVisible
        assertThat(initialIsAlertDialogRationaleVisible).isFalse()
        remindersViewModel.onAction(RemindersAction.OnIsAlertDialogRationaleVisibleUpdate)
        val updatedIsAlertDialogRationaleVisible = remindersViewModel.state.value.isAlertDialogRationaleVisible
        assertThat(updatedIsAlertDialogRationaleVisible).isTrue()
    }

    @Test
    fun `onIsAlertDialogRationaleVisibleUpdate sets isAlertDialogRationaleVisible to false`() = runTest {
        every { remindersRepository.getAllReminders() } returns emptyFlow()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            remindersViewModel.state.collect { }
        }
        val initialIsAlertDialogRationaleVisible = remindersViewModel.state.value.isAlertDialogRationaleVisible
        assertThat(initialIsAlertDialogRationaleVisible).isFalse()
        remindersViewModel.onAction(RemindersAction.OnIsAlertDialogRationaleVisibleUpdate)
        val updatedIsAlertDialogRationaleVisible = remindersViewModel.state.value.isAlertDialogRationaleVisible
        assertThat(updatedIsAlertDialogRationaleVisible).isTrue()
        remindersViewModel.onAction(RemindersAction.OnIsAlertDialogRationaleVisibleUpdate)
        val alertDialogRationaleIsVisible = remindersViewModel.state.value.isAlertDialogRationaleVisible
        assertThat(alertDialogRationaleIsVisible).isFalse()
    }

}