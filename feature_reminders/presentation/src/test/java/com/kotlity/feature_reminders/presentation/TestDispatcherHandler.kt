package com.kotlity.feature_reminders.presentation

import com.kotlity.core.domain.util.DispatcherHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

class TestDispatcherHandler(testDispatcher: TestDispatcher): DispatcherHandler {

    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
}