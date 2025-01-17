package com.kotlity.core.alarm

import com.google.common.truth.Truth.assertThat
import com.kotlity.core.Periodicity
import com.kotlity.core.Reminder
import com.kotlity.core.alarm.di.testSchedulerModule
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.Result
import com.kotlity.utils.KoinDependencyProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
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

private val mockReminderToUpdate = mockReminders[3].copy(
    title = "updated title",
    periodicity = Periodicity.DAILY
)

private val mockReminderToAdd = Reminder(
    id = mockReminders.lastIndex.toLong() + 1,
    title = "mock title",
    reminderTime = mockReminders.last().reminderTime + 3600 * 1000,
    periodicity = Periodicity.DAILY
)

class SchedulerTest: KoinDependencyProvider(modules = listOf(testSchedulerModule)) {

    private val myTestScheduler by inject<TestScheduler>()

    @Test
    fun `initial state of reminders is Result dot Success with no data`() = runTest {
        val expectedResult = Result.Success<List<Reminder>>(data = emptyList())

        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(expectedResult)
        assertThat(result.getData).isEmpty()
    }

    @Test
    fun `adding reminder successfully returns Result dot Success`() = runTest {

        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        val addingResult = myTestScheduler.addOrUpdateReminder(reminder = mockReminderToAdd)
        assertThat(addingResult).isEqualTo(Result.Success(data = Unit))

        val finalResult = myTestScheduler.getReminders().first().getData
        assertThat(finalResult).contains(mockReminderToAdd)

        val lastElement = finalResult.last()
        assertThat(lastElement).isEqualTo(mockReminderToAdd)
    }

    @Test
    fun `updating reminder successfully returns Result dot Success`() = runTest {
        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        val updatingResult = myTestScheduler.addOrUpdateReminder(reminder = mockReminderToUpdate)
        assertThat(updatingResult).isEqualTo(Result.Success(data = Unit))

        val finalResult = myTestScheduler.getReminders().first().getData
        assertThat(finalResult).contains(mockReminderToUpdate)

        val updatedMockReminderIndex = finalResult.indexOf(mockReminderToUpdate)
        assertThat(updatedMockReminderIndex).isEqualTo(3)
        assertThat(mockReminderToUpdate.id).isEqualTo(4)
    }

    @Test
    fun `cancelling reminder successfully returns Result dot Success`() = runTest {
        val mockCancellingReminder = mockReminders[4]
        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        val cancellingResult = myTestScheduler.cancelReminder(id = mockCancellingReminder.id)
        assertThat(cancellingResult).isEqualTo(Result.Success(data = Unit))

        val finalResult = myTestScheduler.getReminders().first().getData
        assertThat(finalResult).doesNotContain(mockCancellingReminder)
    }

    @Test
    fun `adding reminder failed returns Result dot Error with AlarmError dot Security`() = runTest {
        val addingExpectedResult = Result.Error(error = AlarmError.SECURITY)
        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        myTestScheduler.updateRemindersState(result = addingExpectedResult)

        val addingResult = myTestScheduler.addOrUpdateReminder(reminder = mockReminderToAdd)
        assertThat(addingResult).isEqualTo(addingExpectedResult)

        val finalResult = myTestScheduler.getReminders().first()
        assertThat(finalResult).isEqualTo(addingExpectedResult)
    }

    @Test
    fun `adding reminder failed returns Result dot Error with AlarmError dot IllegalArgument`() = runTest {
        val mockReminderToAdd = mockReminders.first().copy(id = -10)
        val addingExpectedResult = Result.Error(error = AlarmError.ILLEGAL_ARGUMENT)
        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        myTestScheduler.updateRemindersState(result = addingExpectedResult)

        val addingResult = myTestScheduler.addOrUpdateReminder(reminder = mockReminderToAdd)
        assertThat(addingResult).isEqualTo(addingExpectedResult)

        val finalResult = myTestScheduler.getReminders().first()
        assertThat(finalResult).isEqualTo(addingExpectedResult)
    }

    @Test
    fun `updating reminder failed returns Result dot Error with AlarmError dot Security`() = runTest {
        val updatingExpectedResult = Result.Error(error = AlarmError.SECURITY)
        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        myTestScheduler.updateRemindersState(result = updatingExpectedResult)

        val updatingResult = myTestScheduler.addOrUpdateReminder(reminder = mockReminderToUpdate)
        assertThat(updatingResult).isEqualTo(updatingExpectedResult)

        val finalResult = myTestScheduler.getReminders().first()
        assertThat(finalResult).isEqualTo(updatingExpectedResult)
    }

    @Test
    fun `updating reminder failed returns Result dot Error with AlarmError dot Canceled`() = runTest {
        val updatingExpectedResult = Result.Error(error = AlarmError.CANCELED)
        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        myTestScheduler.updateRemindersState(result = updatingExpectedResult)

        val updatingResult = myTestScheduler.addOrUpdateReminder(reminder = mockReminderToUpdate)
        assertThat(updatingResult).isEqualTo(updatingExpectedResult)

        val finalResult = myTestScheduler.getReminders().first()
        assertThat(finalResult).isEqualTo(updatingExpectedResult)
    }

    @Test
    fun `cancelling reminder failed returns Result dot Error with AlarmError dot IllegalArgument`() = runTest {
        val cancellingExpectedResult = Result.Error(error = AlarmError.ILLEGAL_ARGUMENT)
        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        val cancellingResult = myTestScheduler.cancelReminder(id = mockReminders.last().id + 1)
        assertThat(cancellingResult).isEqualTo(cancellingExpectedResult)

        val finalResult = myTestScheduler.getReminders().first()
        assertThat(finalResult).isEqualTo(cancellingExpectedResult)
    }

    @Test
    fun `cancelling reminder failed returns Result dot Error with AlarmError dot Unknown`() = runTest {
        val cancellingExpectedResult = Result.Error(error = AlarmError.UNKNOWN)
        val setRemindersExpectedResult = Result.Success(data = mockReminders)

        myTestScheduler.setReminders(reminders = mockReminders)
        val result = myTestScheduler.getReminders().first()
        assertThat(result).isEqualTo(setRemindersExpectedResult)
        assertThat(result.getData).isEqualTo(mockReminders)

        myTestScheduler.updateRemindersState(result = cancellingExpectedResult)

        val cancellingResult = myTestScheduler.cancelReminder(id = mockReminderToUpdate.id)
        assertThat(cancellingResult).isEqualTo(cancellingExpectedResult)

        val finalResult = myTestScheduler.getReminders().first()
        assertThat(finalResult).isEqualTo(cancellingExpectedResult)
    }
}