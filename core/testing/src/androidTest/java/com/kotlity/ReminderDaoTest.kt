package com.kotlity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.Periodicity
import com.kotlity.core.local.ReminderDao
import com.kotlity.core.local.ReminderDatabase
import com.kotlity.core.local.ReminderEntity
import com.kotlity.di.db.testReminderDaoModule
import com.kotlity.di.db.testReminderDatabaseModule
import com.kotlity.di.testDispatcherHandlerModule
import com.kotlity.utils.AndroidKoinDependencyProvider
import com.kotlity.utils.TestRuleProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.test.inject

@SmallTest
class ReminderDaoTest: AndroidKoinDependencyProvider(
    modules = listOf(
        testReminderDatabaseModule,
        testReminderDaoModule,
        testDispatcherHandlerModule
    )
), TestRuleProvider {

    private val reminderDatabase by inject<ReminderDatabase>()
    private val reminderDao by inject<ReminderDao>()

    @After
    fun teardown() {
        reminderDatabase.apply {
            clearAllTables()
            close()
        }
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertRemindersSuccessfully() = runTest {
        val reminders = listOf(
            ReminderEntity(
                id = 0,
                title = "mock reminder title1",
                reminderTime = 13313123L,
                periodicity = Periodicity.ONCE
            ),
            ReminderEntity(
                id = 1,
                title = "mock reminder title2",
                reminderTime = 13313164L,
                periodicity = Periodicity.DAILY
            ),
            ReminderEntity(
                id = 2,
                title = "mock reminder title3",
                reminderTime = 13313190L,
                periodicity = Periodicity.WEEKDAYS
            )
        )
        reminders.forEach { reminder ->
            reminderDao.upsertReminder(reminder)
        }
        reminderDao.getAllReminders().test {
            val dbReminders = awaitItem()
            assertThat(dbReminders).isEqualTo(reminders)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun updateReminderSuccessfully() = runTest {
        val reminder = ReminderEntity(
            id = 0,
            title = "mock reminder title",
            reminderTime = 13313123L,
            periodicity = Periodicity.ONCE
        )
        reminderDao.upsertReminder(reminder)
        val dbReminders = reminderDao.getAllReminders().first()
        assertThat(dbReminders).contains(reminder)
        val updatedReminder = reminder.copy(title = "updated mock reminder title")
        reminderDao.upsertReminder(updatedReminder)
        val updatedDBReminders = reminderDao.getAllReminders().first()
        assertThat(updatedDBReminders).doesNotContain(reminder)
        assertThat(updatedDBReminders).contains(updatedReminder)
    }

    @Test
    fun deleteReminderSuccessfully() = runTest {
        val reminder = ReminderEntity(
            id = 0,
            title = "mock reminder title",
            reminderTime = 13313123L,
            periodicity = Periodicity.ONCE
        )
        reminderDao.upsertReminder(reminder)
        val dbReminders = reminderDao.getAllReminders().first()
        assertThat(dbReminders).contains(reminder)
        reminderDao.deleteReminder(reminder.id!!)
        val emptyDBReminders = reminderDao.getAllReminders().first()
        assertThat(emptyDBReminders).doesNotContain(reminder)
        assertThat(emptyDBReminders).isEmpty()
    }

}