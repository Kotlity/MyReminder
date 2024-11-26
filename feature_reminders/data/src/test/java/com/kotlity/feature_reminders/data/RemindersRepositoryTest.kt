package com.kotlity.feature_reminders.data

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.alarm.domain.Scheduler
import com.kotlity.core.data.local.ReminderDao
import com.kotlity.core.data.local.ReminderEntity
import com.kotlity.core.data.local.toReminder
import com.kotlity.core.domain.Periodicity
import com.kotlity.core.domain.util.AlarmError
import com.kotlity.core.domain.util.DatabaseError
import com.kotlity.core.domain.util.DispatcherHandler
import com.kotlity.core.domain.util.ReminderError
import com.kotlity.core.domain.util.Result
import com.kotlity.feature_reminders.data.di.testDispatcherHandlerModule
import com.kotlity.feature_reminders.domain.RemindersRepository
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

@OptIn(ExperimentalCoroutinesApi::class)
class RemindersRepositoryTest: KoinTest {

    private val reminderDao: ReminderDao = mockk()

    private val scheduler: Scheduler = mockk()

    private val dispatcherHandler by inject<DispatcherHandler>()
    private lateinit var remindersRepository: RemindersRepository

    private val mockReminderEntities = listOf(
        ReminderEntity(
            id = 0,
            title = "test title1",
            reminderTime = 234234234,
            periodicity = Periodicity.ONCE
        ),
        ReminderEntity(
            id = 1,
            title = "test title2",
            reminderTime = 234234256,
            periodicity = Periodicity.DAILY
        ),
        ReminderEntity(
            id = 2,
            title = "test title3",
            reminderTime = 234234289,
            periodicity = Periodicity.WEEKDAYS
        )
    )

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(testDispatcherHandlerModule)
    }

    @get:Rule
    val mockKRule = MockKRule(this)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcherHandler.io)
        remindersRepository = RemindersRepositoryImplementation(reminderDao, scheduler, dispatcherHandler)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllReminders initial state is loading`() = runTest {
        every { reminderDao.getAllReminders() } returns flowOf(mockReminderEntities)
        remindersRepository.getAllReminders().test {
            val loadingState = awaitItem()
            verify(exactly = 1) { reminderDao.getAllReminders() }
            assertThat(loadingState).isInstanceOf(Result.Loading::class.java)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAllReminders with empty list when no reminders available returns success`() = runTest {
        every { reminderDao.getAllReminders() } returns flowOf(emptyList())
        remindersRepository.getAllReminders().test {
            awaitItem()
            val result = awaitItem()
            verify(exactly = 1) { reminderDao.getAllReminders() }
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).isEmpty()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAllReminders with data returns success`() = runTest {
        every { reminderDao.getAllReminders() } returns flowOf(mockReminderEntities)
        remindersRepository.getAllReminders().test {
            awaitItem()
            val result = awaitItem()
            verify(exactly = 1) { reminderDao.getAllReminders() }
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).isEqualTo(mockReminderEntities.map { it.toReminder() })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAllReminders throws IllegalStateException`() = runTest {
        every { reminderDao.getAllReminders() } throws IllegalStateException()
        remindersRepository.getAllReminders().test {
            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Result.Loading::class.java)
            val result = awaitItem()
            verify(exactly = 1) { reminderDao.getAllReminders() }
            assertThat(result).isInstanceOf(Result.Error::class.java)
            assertThat((result as Result.Error).error).isEqualTo(DatabaseError.ILLEGAL_STATE)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAllReminders throws SQLiteConstraintException`() = runTest {
        every { reminderDao.getAllReminders() } throws SQLiteConstraintException()
        remindersRepository.getAllReminders().test {
            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Result.Loading::class.java)
            val result = awaitItem()
            verify(exactly = 1) { reminderDao.getAllReminders() }
            assertThat(result).isInstanceOf(Result.Error::class.java)
            assertThat((result as Result.Error).error).isEqualTo(DatabaseError.SQLITE_CONSTRAINT)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `deleteReminder returns success`() = runTest {
        coJustRun { reminderDao.deleteReminder(any()) }
        every { scheduler.cancelReminder(any()) } returns Result.Success(Unit)
        val result = remindersRepository.deleteReminder(0)
        coVerify(exactly = 1) { reminderDao.deleteReminder(any()) }
        verify(exactly = 1) { scheduler.cancelReminder(any()) }
        assertThat(result).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `deleteReminder throws SQLiteException`() = runTest {
        coEvery { reminderDao.deleteReminder(any()) } throws SQLiteException()
        val result = remindersRepository.deleteReminder(0)
        coVerify(exactly = 1) { reminderDao.deleteReminder(any()) }
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(ReminderError.Database(DatabaseError.SQLITE_EXCEPTION))
    }

    @Test
    fun `deleteReminder throws SecurityException`() = runTest {
        coJustRun { reminderDao.deleteReminder(any()) }
        every { scheduler.cancelReminder(any()) } throws SecurityException()
        val result = remindersRepository.deleteReminder(0)
        coVerify(exactly = 1) { reminderDao.deleteReminder(any()) }
        verify(exactly = 1) { scheduler.cancelReminder(any()) }
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(ReminderError.Alarm(AlarmError.SECURITY))
    }

}