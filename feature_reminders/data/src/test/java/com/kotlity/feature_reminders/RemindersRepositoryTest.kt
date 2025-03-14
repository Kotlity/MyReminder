package com.kotlity.feature_reminders

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.Periodicity
import com.kotlity.core.alarm.Scheduler
import com.kotlity.core.local.ReminderDao
import com.kotlity.core.local.ReminderEntity
import com.kotlity.core.local.toReminder
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.DispatcherHandler
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result
import com.kotlity.di.testDispatcherHandlerModule
import com.kotlity.utils.KoinDependencyProvider
import com.kotlity.utils.TestRuleProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.koin.test.inject

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

class RemindersRepositoryTest: KoinDependencyProvider(modules = listOf(testDispatcherHandlerModule)), TestRuleProvider {

    @RelaxedMockK
    private lateinit var reminderDao: ReminderDao

    @MockK
    private lateinit var scheduler: Scheduler

    private val dispatcherHandler by inject<DispatcherHandler>()
    private lateinit var remindersRepository: RemindersRepository

    @Before
    fun setup() {
        remindersRepository = RemindersRepositoryImplementation(reminderDao, scheduler, dispatcherHandler)
    }

    @Test
    fun `getAllReminders initial state is loading`() = runTest {
        every { reminderDao.getAllReminders() } returns flowOf(mockReminderEntities)
        remindersRepository.getAllReminders().test {
            val loadingState = awaitItem()
            assertThat(loadingState).isInstanceOf(Result.Loading::class.java)
            verify(exactly = 1) { reminderDao.getAllReminders() }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAllReminders with empty list when no reminders available returns success`() = runTest {
        every { reminderDao.getAllReminders() } returns flowOf(emptyList())
        remindersRepository.getAllReminders().test {
            awaitItem()
            val result = awaitItem()
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).isEmpty()
            verify(exactly = 1) { reminderDao.getAllReminders() }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAllReminders with data returns success`() = runTest {
        every { reminderDao.getAllReminders() } returns flowOf(mockReminderEntities)
        remindersRepository.getAllReminders().test {
            awaitItem()
            val result = awaitItem()
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).isEqualTo(mockReminderEntities.map { it.toReminder() })
            verify(exactly = 1) { reminderDao.getAllReminders() }
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
            assertThat(result).isInstanceOf(Result.Error::class.java)
            assertThat((result as Result.Error).error).isEqualTo(DatabaseError.ILLEGAL_STATE)
            verify(exactly = 1) { reminderDao.getAllReminders() }
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
            assertThat(result).isInstanceOf(Result.Error::class.java)
            assertThat((result as Result.Error).error).isEqualTo(DatabaseError.SQLITE_CONSTRAINT)
            verify(exactly = 1) { reminderDao.getAllReminders() }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `deleteReminder successfully delete reminder and returns it`() = runTest {
        val mockReminderToDelete = mockReminderEntities[0]
        every { scheduler.cancelReminder(any()) } returns Result.Success(Unit)
        coEvery { reminderDao.getReminderById(any()) } returns mockReminderToDelete
        val result = remindersRepository.deleteReminder(mockReminderToDelete.id!!)
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(mockReminderToDelete.toReminder())
        verify(exactly = 1) { scheduler.cancelReminder(any()) }
        coVerify(exactly = 1) { reminderDao.getReminderById(any()) }
        coVerify(exactly = 1) { reminderDao.deleteReminder(any()) }
    }

    @Test
    fun `deleteReminder returns DatabaseError dot SQLITE_EXCEPTION`() = runTest {
        every { scheduler.cancelReminder(any()) } returns Result.Success(data = Unit)
        coEvery { reminderDao.getReminderById(any()) } returns null
        coEvery { reminderDao.deleteReminder(any()) } throws SQLiteException()
        val result = remindersRepository.deleteReminder(0)
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(ReminderError.Database(DatabaseError.SQLITE_EXCEPTION))
        verify(exactly = 1) { scheduler.cancelReminder(any()) }
        coVerify(exactly = 1) { reminderDao.getReminderById(any()) }
        coVerify(exactly = 1) { reminderDao.deleteReminder(any()) }
    }

    @Test
    fun `deleteReminder returns AlarmError dot SECURITY`() = runTest {
        every { scheduler.cancelReminder(any()) } returns Result.Error(AlarmError.SECURITY)
        val result = remindersRepository.deleteReminder(0)
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).error).isEqualTo(ReminderError.Alarm(AlarmError.SECURITY))
        verify(exactly = 1) { scheduler.cancelReminder(any()) }
    }

    @Test
    fun `restoreReminder successfully restored reminder`() = runTest {
        every { scheduler.addOrUpdateReminder(any()) } returns Result.Success(data = Unit)
        val result = remindersRepository.restoreReminder(mockReminderEntities.last().toReminder())
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify(exactly = 1) { reminderDao.upsertReminder(any()) }
        verify(exactly = 1) { scheduler.addOrUpdateReminder(any()) }
    }

}