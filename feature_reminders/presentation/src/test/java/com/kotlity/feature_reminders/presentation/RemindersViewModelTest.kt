package com.kotlity.feature_reminders.presentation

import com.kotlity.feature_reminders.domain.RemindersRepository
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RemindersViewModelTest {

    @MockK
    private lateinit var remindersRepository: RemindersRepository

    private lateinit var remindersViewModel: RemindersViewModel

    @get:Rule
    val mockKRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        remindersViewModel = RemindersViewModel(remindersRepository)
    }

}