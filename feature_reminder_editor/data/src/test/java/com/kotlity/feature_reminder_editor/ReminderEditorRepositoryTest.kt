package com.kotlity.feature_reminder_editor

import android.database.sqlite.SQLiteConstraintException
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.Periodicity
import com.kotlity.core.alarm.Scheduler
import com.kotlity.core.local.ReminderDao
import com.kotlity.core.local.ReminderEntity
import com.kotlity.core.local.toReminder
import com.kotlity.core.local.toReminderEntity
import com.kotlity.core.util.AlarmError
import com.kotlity.core.util.DatabaseError
import com.kotlity.core.util.DispatcherHandler
import com.kotlity.core.util.ReminderError
import com.kotlity.core.util.Result
import com.kotlity.di.testDispatcherHandlerModule
import com.kotlity.utils.KoinDependencyProvider
import com.kotlity.utils.MainDispatcherRule
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.inject

private val mockReminderEntity = ReminderEntity(
    id = 5,
    title = "mock title",
    reminderTime = System.currentTimeMillis() + 3600 * 1000,
    periodicity = Periodicity.DAILY
)

private val updatedReminder = mockReminderEntity.toReminder().copy(
    title = "mock title updated",
    reminderTime = mockReminderEntity.reminderTime + 3600 * 1000
)

class ReminderEditorRepositoryTest: KoinDependencyProvider(modules = listOf(testDispatcherHandlerModule)) {

    @MockK
    private lateinit var alarmScheduler: Scheduler

    @MockK(relaxUnitFun = true)
    private lateinit var reminderDao: ReminderDao

    private val dispatcherHandler by inject<DispatcherHandler>()

    private lateinit var reminderEditorRepository: ReminderEditorRepository

    @get:Rule(order = 1)
    val mockKRule = MockKRule(this)

    @get:Rule(order = 2)
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        reminderEditorRepository = ReminderEditorRepositoryImplementation(
            alarmScheduler = alarmScheduler,
            reminderDao = reminderDao,
            dispatcherHandler = dispatcherHandler
        )
    }

    @Test
    fun `getReminderById returns null reminder`() = runTest {
        coEvery { reminderDao.getReminderById(id = any()) } returns null

        val result = reminderEditorRepository.getReminderById(id = 3)

        assertThat(result is Result.Success).isTrue()
        assertThat((result as Result.Success).data).isNull()
        coVerify(exactly = 1) { reminderDao.getReminderById(id = any()) }
    }

    @Test
    fun `getReminderById returns not null reminder`() = runTest {
        coEvery { reminderDao.getReminderById(id = mockReminderEntity.id!!) } returns mockReminderEntity

        val result = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)
        val expectedResult = mockReminderEntity.toReminder()

        assertThat(result is Result.Success).isTrue()
        assertThat((result as Result.Success).data).isEqualTo(expectedResult)
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }
    }

    @Test
    fun `getReminderById returns DatabaseError dot ILLEGAL_STATE`() = runTest {
        coEvery { reminderDao.getReminderById(id = any()) } throws IllegalStateException()

        val result = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)
        val expectedResult = DatabaseError.ILLEGAL_STATE

        assertThat(result is Result.Error).isTrue()
        assertThat((result as Result.Error).error).isEqualTo(expectedResult)
        coVerify(exactly = 1) { reminderDao.getReminderById(id = any()) }
    }

    @Test
    fun `successful insertion of a new reminder and its return`() = runTest {
        coEvery { reminderDao.getReminderById(id = mockReminderEntity.id!!) } returns null
        val insertedReminder = mockReminderEntity.toReminder()

        val initialResult = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)

        assertThat(initialResult is Result.Success).isTrue()
        assertThat((initialResult as Result.Success).data).isNull()
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }

        every { alarmScheduler.addOrUpdateReminder(reminder = insertedReminder) } returns Result.Success(data = Unit)

        val result = reminderEditorRepository.upsertReminder(reminder = insertedReminder)

        assertThat(result is Result.Success).isTrue()
        verify(exactly = 1) { alarmScheduler.addOrUpdateReminder(reminder = insertedReminder) }

        clearMocks(firstMock = reminderDao)
        coEvery { reminderDao.getReminderById(id = mockReminderEntity.id!!) } returns mockReminderEntity

        val finalResult = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)

        assertThat(finalResult is Result.Success).isTrue()
        assertThat((finalResult as Result.Success).data).isEqualTo(insertedReminder)
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }
    }

    @Test
    fun `successful reminder update and its return`() = runTest {
        coEvery { reminderDao.getReminderById(id = mockReminderEntity.id!!) } returns mockReminderEntity

        val initialResult = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)

        assertThat(initialResult is Result.Success).isTrue()
        assertThat((initialResult as Result.Success).data).isEqualTo(mockReminderEntity.toReminder())
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }

        every { alarmScheduler.addOrUpdateReminder(reminder = updatedReminder) } returns Result.Success(data = Unit)

        val result = reminderEditorRepository.upsertReminder(reminder = updatedReminder)

        assertThat(result is Result.Success).isTrue()
        verify(exactly = 1) { alarmScheduler.addOrUpdateReminder(reminder = updatedReminder) }

        clearMocks(firstMock = reminderDao)
        coEvery { reminderDao.getReminderById(id = mockReminderEntity.id!!) } returns updatedReminder.toReminderEntity()

        val finalResult = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)

        assertThat(finalResult is Result.Success).isTrue()
        assertThat((finalResult as Result.Success).data).isEqualTo(updatedReminder)
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }
    }

    @Test
    fun `attempt to add a new reminder returns AlarmError dot SECURITY`() = runTest {
        coEvery { reminderDao.getReminderById(mockReminderEntity.id!!) } returns null

        val initialResult = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)

        assertThat(initialResult is Result.Success).isTrue()
        assertThat((initialResult as Result.Success).data).isNull()
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }

        every { alarmScheduler.addOrUpdateReminder(reminder = any()) } returns Result.Error(error = AlarmError.SECURITY)

        val result = reminderEditorRepository.upsertReminder(reminder = mockReminderEntity.toReminder())

        assertThat(result is Result.Error).isTrue()

        val reminderError = (result as Result.Error).error

        assertThat(reminderError).isInstanceOf(ReminderError.Alarm::class.java)
        assertThat((reminderError as ReminderError.Alarm).error).isEqualTo(AlarmError.SECURITY)
        verify(exactly = 1) { alarmScheduler.addOrUpdateReminder(reminder = any()) }
    }

    @Test
    fun `attempt to add a new reminder returns DatabaseError dot SQLITE_CONSTRAINT`() = runTest {
        coEvery { reminderDao.getReminderById(mockReminderEntity.id!!) } returns null

        val initialResult = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)

        assertThat(initialResult is Result.Success).isTrue()
        assertThat((initialResult as Result.Success).data).isNull()
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }

        every { alarmScheduler.addOrUpdateReminder(reminder = mockReminderEntity.toReminder()) } returns Result.Success(data = Unit)
        coEvery { reminderDao.upsertReminder(entity = mockReminderEntity) } throws SQLiteConstraintException()

        val result = reminderEditorRepository.upsertReminder(reminder = mockReminderEntity.toReminder())

        assertThat(result is Result.Error).isTrue()

        val reminderError = (result as Result.Error).error

        assertThat(reminderError).isInstanceOf(ReminderError.Database::class.java)
        assertThat((reminderError as ReminderError.Database).error).isEqualTo(DatabaseError.SQLITE_CONSTRAINT)
        verify(exactly = 1) { alarmScheduler.addOrUpdateReminder(reminder = mockReminderEntity.toReminder()) }
        coVerify(exactly = 1) { reminderDao.upsertReminder(entity = mockReminderEntity) }
    }

    @Test
    fun `attempt to update reminder returns AlarmError dot SECURITY`() = runTest {
        coEvery { reminderDao.getReminderById(mockReminderEntity.id!!) } returns mockReminderEntity

        val initialResult = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)

        assertThat(initialResult is Result.Success).isTrue()
        assertThat((initialResult as Result.Success).data).isEqualTo(mockReminderEntity.toReminder())
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }

        every { alarmScheduler.addOrUpdateReminder(reminder = updatedReminder) } returns Result.Error(error = AlarmError.SECURITY)

        val result = reminderEditorRepository.upsertReminder(reminder = updatedReminder)

        assertThat(result is Result.Error).isTrue()

        val reminderError = (result as Result.Error).error

        assertThat(reminderError).isInstanceOf(ReminderError.Alarm::class.java)
        assertThat((reminderError as ReminderError.Alarm).error).isEqualTo(AlarmError.SECURITY)
        verify(exactly = 1) { alarmScheduler.addOrUpdateReminder(reminder = updatedReminder) }
    }

    @Test
    fun `attempt to update reminder returns DatabaseError dot SQLITE_CONSTRAINT`() = runTest {
        coEvery { reminderDao.getReminderById(mockReminderEntity.id!!) } returns mockReminderEntity

        val initialResult = reminderEditorRepository.getReminderById(id = mockReminderEntity.id!!)

        assertThat(initialResult is Result.Success).isTrue()
        assertThat((initialResult as Result.Success).data).isEqualTo(mockReminderEntity.toReminder())
        coVerify(exactly = 1) { reminderDao.getReminderById(id = mockReminderEntity.id!!) }

        every { alarmScheduler.addOrUpdateReminder(reminder = updatedReminder) } returns Result.Success(data = Unit)
        coEvery { reminderDao.upsertReminder(entity = updatedReminder.toReminderEntity()) } throws SQLiteConstraintException()

        val result = reminderEditorRepository.upsertReminder(reminder = updatedReminder)

        assertThat(result is Result.Error).isTrue()

        val reminderError = (result as Result.Error).error

        assertThat(reminderError).isInstanceOf(ReminderError.Database::class.java)
        assertThat((reminderError as ReminderError.Database).error).isEqualTo(DatabaseError.SQLITE_CONSTRAINT)
        verify(exactly = 1) { alarmScheduler.addOrUpdateReminder(reminder = updatedReminder) }
        coVerify(exactly = 1) { reminderDao.upsertReminder(entity = updatedReminder.toReminderEntity()) }
    }
}