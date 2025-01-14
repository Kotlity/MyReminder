package com.kotlity.feature_reminders

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kotlity.TimeFormatter
import com.kotlity.core.Periodicity
import com.kotlity.core.Reminder
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result
import com.kotlity.core.util.Event
import com.kotlity.feature_reminders.actions.RemindersAction
import com.kotlity.feature_reminders.di.testRemindersRepositoryModule
import com.kotlity.feature_reminders.events.ReminderOneTimeEvent
import com.kotlity.feature_reminders.mappers.toReminderUi
import com.kotlity.feature_reminders.states.SelectedReminderState
import com.kotlity.utils.KoinDependencyProvider
import com.kotlity.utils.MainDispatcherRule
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

private val mockReminders = (1..10).map { index ->
    Reminder(
        id = index.toLong(),
        title = "title$index",
        reminderTime = System.currentTimeMillis() + index.toLong() * 10000,
        periodicity = if (index % 2 == 0) Periodicity.WEEKDAYS else Periodicity.ONCE
    )
}

private val mockReminderToDelete = mockReminders[1]

private val mockSelectedReminderState = SelectedReminderState(id = 1, position = Pair(first = 128, 356))


@OptIn(ExperimentalCoroutinesApi::class)
class RemindersViewModelTest: KoinDependencyProvider(modules = listOf(testRemindersRepositoryModule)) {

    @RelaxedMockK
    private lateinit var timeFormatter: TimeFormatter

    private val testRemindersRepository by inject<TestRemindersRepository>()
    private lateinit var remindersViewModel: RemindersViewModel

    @get:Rule
    val mockKRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(coroutineDispatcher = UnconfinedTestDispatcher())

    @Before
    fun setup() {
        remindersViewModel = RemindersViewModel(
            remindersRepository = testRemindersRepository,
            timeFormatter = timeFormatter
        )
    }

    @Test
    fun `on initial call onLoadReminders set state to loading`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(true)
        assertThat(remindersViewModel.state.value.isLoading).isFalse()

        remindersViewModel.state.test {
            val initialValue = awaitItem()
            assertThat(initialValue.isLoading).isTrue()
            cancelAndConsumeRemainingEvents()
        }
        verify(exactly = 1) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `onLoadReminders returns reminders with 24 hour format`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(true)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(reminders = mockReminders)

        val is24HourFormat = timeFormatter.is24HourFormat.first()
        val remindersState = remindersViewModel.state.value
        assertThat(remindersState.isLoading).isFalse()
        assertThat(remindersState.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormat) })
        verify(exactly = 2) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `onLoadReminders returns reminders with 12 hour format`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(false)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(reminders = mockReminders)

        val is24HourFormat = timeFormatter.is24HourFormat.first()
        val remindersState = remindersViewModel.state.value
        assertThat(remindersState.isLoading).isFalse()
        assertThat(remindersState.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormat) })
        verify(exactly = 2) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `onLoadReminders returns reminders with 24 hour format after changing it from 12 to 24 hour format`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(false)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(reminders = mockReminders)

        val is24HourFormatInitially = timeFormatter.is24HourFormat.first()
        val remindersStateInitially = remindersViewModel.state.value
        assertThat(remindersStateInitially.isLoading).isFalse()
        assertThat(remindersStateInitially.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormatInitially) })

        timeFormatter.is24HourFormatChanged(update = true)

        val is24HourFormatFinally = timeFormatter.is24HourFormat.first()
        val remindersStateFinally = remindersViewModel.state.value
        assertThat(remindersStateFinally.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormatFinally) })

        verify(exactly = 3) { timeFormatter.is24HourFormat }
        verify(exactly = 1) { timeFormatter.is24HourFormatChanged(update = true) }
    }

    @Test
    fun `onLoadReminders returns reminders with 12 hour format after changing it from 24 to 12 hour format`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(true)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(reminders = mockReminders)

        val is24HourFormatInitially = timeFormatter.is24HourFormat.first()
        val remindersStateInitially = remindersViewModel.state.value
        assertThat(remindersStateInitially.isLoading).isFalse()
        assertThat(remindersStateInitially.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormatInitially) })

        timeFormatter.is24HourFormatChanged(update = false)

        val is24HourFormatFinally = timeFormatter.is24HourFormat.first()
        val remindersStateFinally = remindersViewModel.state.value
        assertThat(remindersStateFinally.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormatFinally) })

        verify(exactly = 3) { timeFormatter.is24HourFormat }
        verify(exactly = 1) { timeFormatter.is24HourFormatChanged(update = false) }
    }

    @Test
    fun `onLoadReminders returns DatabaseError dot SQLiteException and send it to the eventChannel`() = runTest {
        val mockError = ReminderError.Database(DatabaseError.SQLITE_EXCEPTION)

        every { timeFormatter.is24HourFormat } returns flowOf(true)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminderError(error = mockError)
        testRemindersRepository.updateObservableReminders(result = Result.Error(mockError.error))

        assertThat(remindersViewModel.state.value.isLoading).isFalse()

        val sentError = remindersViewModel.eventFlow.first()
        assertThat(sentError).isInstanceOf(Event.Error::class.java)
        assertThat((sentError as Event.Error).error).isEqualTo(mockError)
        verify(exactly = 1) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `onReminderDelete returns successful result and send ReminderOneTimeEvent dot Delete to the eventChannel`() = runTest {
        val updatedMockReminders = mockReminders.toMutableList().apply { removeIf { it == mockReminderToDelete } }

        every { timeFormatter.is24HourFormat } returns flowOf(true)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(reminders = mockReminders)

        remindersViewModel.onAction(RemindersAction.OnReminderDelete(id = mockReminderToDelete.id))

        val is24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(remindersViewModel.state.value.reminders).isEqualTo(updatedMockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormat) })

        val channelResult = remindersViewModel.eventFlow.first()
        assertThat(channelResult).isInstanceOf(Event.Success::class.java)
        assertThat((channelResult as Event.Success).data).isInstanceOf(ReminderOneTimeEvent.Delete::class.java)
        verify(exactly = 2) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `onReminderDelete returns DatabaseError dot IllegalArgument and send it to the eventChannel`() = runTest {
        val mockError = ReminderError.Database(error = DatabaseError.ILLEGAL_ARGUMENT)

        every { timeFormatter.is24HourFormat } returns flowOf(false)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(reminders = mockReminders)
        testRemindersRepository.setReminderError(mockError)

        remindersViewModel.onAction(RemindersAction.OnReminderDelete(id = mockReminderToDelete.id))

        val is24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(remindersViewModel.state.value.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormat) })

        val channelResult = remindersViewModel.eventFlow.first()
        assertThat(channelResult).isInstanceOf(Event.Error::class.java)
        assertThat((channelResult as Event.Error).error).isEqualTo(mockError)
        verify(exactly = 2) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `onReminderDelete returns AlarmError dot Security and send it to the eventChannel`() = runTest {
        val mockError = ReminderError.Alarm(error = AlarmError.SECURITY)

        every { timeFormatter.is24HourFormat } returns flowOf(true)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(reminders = mockReminders)
        testRemindersRepository.setReminderError(mockError)

        remindersViewModel.onAction(RemindersAction.OnReminderDelete(id = mockReminderToDelete.id))

        val is24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(remindersViewModel.state.value.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormat) })

        val channelResult = remindersViewModel.eventFlow.first()
        assertThat(channelResult).isInstanceOf(Event.Error::class.java)
        assertThat((channelResult as Event.Error).error).isEqualTo(mockError)
        verify(exactly = 2) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `successfully deletion of several reminders`() = runTest {
        val mockRemindersToDelete = mockReminders.filter { it.id.toInt() == 1 || it.id.toInt() == 2 || it.id.toInt() == 4 }

        every { timeFormatter.is24HourFormat } returns flowOf(false)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(mockReminders)

        mockRemindersToDelete.forEach { reminder ->
            remindersViewModel.onAction(RemindersAction.OnReminderDelete(id = reminder.id))
        }
        assertThat(remindersViewModel.state.value.reminders).containsNoneIn(mockRemindersToDelete)
        verify(exactly = 1) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `successfully restoration of the recently deleted reminder`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(false)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        testRemindersRepository.setReminders(reminders = mockReminders)

        val is24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(remindersViewModel.state.value.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormat) })

        launch {
            remindersViewModel.onAction(RemindersAction.OnReminderDelete(id = mockReminderToDelete.id))
            assertThat(remindersViewModel.state.value.reminders).doesNotContain(mockReminderToDelete.toReminderUi(is24HourFormat = is24HourFormat))
            remindersViewModel.onAction(RemindersAction.OnReminderRestore)
        }
        assertThat(remindersViewModel.state.value.reminders).contains(mockReminderToDelete.toReminderUi(is24HourFormat = is24HourFormat))

        assertThat((remindersViewModel.eventFlow.first() as Event.Success).data).isInstanceOf(ReminderOneTimeEvent.Delete::class.java)
        verify(exactly = 2) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `onReminderRestore returns DatabaseError dot Unknown and send it to the eventChannel`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(true)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        val mockReminderError = ReminderError.Database(error = DatabaseError.UNKNOWN)

        testRemindersRepository.setReminders(reminders = mockReminders)

        val is24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(remindersViewModel.state.value.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormat) })

        launch {
            remindersViewModel.onAction(RemindersAction.OnReminderDelete(id = mockReminderToDelete.id))
            assertThat(remindersViewModel.state.value.reminders).doesNotContain(mockReminderToDelete.toReminderUi(is24HourFormat = is24HourFormat))
            testRemindersRepository.setReminderError(error = mockReminderError)
            remindersViewModel.onAction(RemindersAction.OnReminderRestore)
        }
        advanceUntilIdle()
        assertThat(remindersViewModel.state.value.reminders).doesNotContain(mockReminderToDelete.toReminderUi(is24HourFormat = is24HourFormat))

        val eventReminderDelete = (remindersViewModel.eventFlow.first() as Event.Success).data
        val eventReminderRestore = ((remindersViewModel.eventFlow.first() as Event.Error).error as ReminderError.Database).error

        assertThat(eventReminderDelete).isInstanceOf(ReminderOneTimeEvent.Delete::class.java)
        assertThat(eventReminderRestore).isEqualTo(mockReminderError.error)
        verify(exactly = 2) { timeFormatter.is24HourFormat }
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
    fun `onReminderSelect updates selectedReminderId and x and y coordinates with passed id and coordinates`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(false)
        remindersViewModel.state.test {
            assertThat(awaitItem().selectedReminderState).isEqualTo(SelectedReminderState())
            remindersViewModel.onAction(
                RemindersAction.OnReminderSelect(
                    position = mockSelectedReminderState.position!!,
                    id = mockSelectedReminderState.id!!
                )
            )
            assertThat(awaitItem().selectedReminderState).isEqualTo(mockSelectedReminderState)
        }
        verify(exactly = 1) { timeFormatter.is24HourFormat }
    }

    @Test
    fun `onReminderUnselect sets selectedReminderId and x and y coordinates to null`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(false)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        assertThat(remindersViewModel.state.value.selectedReminderState).isEqualTo(SelectedReminderState())

        remindersViewModel.onAction(
            RemindersAction.OnReminderSelect(
                id = mockSelectedReminderState.id!!,
                position = mockSelectedReminderState.position!!
            )
        )

        assertThat(remindersViewModel.state.value.selectedReminderState).isEqualTo(mockSelectedReminderState)

        remindersViewModel.onAction(RemindersAction.OnReminderUnselect)

        assertThat(remindersViewModel.state.value.selectedReminderState).isEqualTo(SelectedReminderState())
    }

    @Test
    fun `onReminderSelect updates selectedReminderState after that onReminderEdit sends ReminderOneTimeEvent dot Edit with passed id and sets selectedReminderState to the initial value`() = runTest {
        every { timeFormatter.is24HourFormat } returns flowOf(false)
        backgroundScope.launch(UnconfinedTestDispatcher()) { remindersViewModel.state.collect() }

        val initialState = remindersViewModel.state.value
        assertThat(initialState.reminders).isEmpty()
        assertThat(initialState.selectedReminderState).isEqualTo(SelectedReminderState())

        testRemindersRepository.setReminders(mockReminders)

        val is24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(remindersViewModel.state.value.reminders).isEqualTo(mockReminders.map { it.toReminderUi(is24HourFormat = is24HourFormat) })

        remindersViewModel.onAction(
            RemindersAction.OnReminderSelect(
                position = mockSelectedReminderState.position!!,
                id = mockSelectedReminderState.id!!
            )
        )
        val selectedReminderState = remindersViewModel.state.value.selectedReminderState
        assertThat(selectedReminderState).isEqualTo(mockSelectedReminderState)

        remindersViewModel.onAction(RemindersAction.OnReminderEdit(id = selectedReminderState.id!!))
        val eventReminderEdit = (remindersViewModel.eventFlow.first() as Event.Success).data
        assertThat(eventReminderEdit).isInstanceOf(ReminderOneTimeEvent.Edit::class.java)
        assertThat((eventReminderEdit as ReminderOneTimeEvent.Edit).id).isEqualTo(selectedReminderState.id)

        assertThat(remindersViewModel.state.value.selectedReminderState).isEqualTo(SelectedReminderState())
        verify(exactly = 2) { timeFormatter.is24HourFormat }
    }
}