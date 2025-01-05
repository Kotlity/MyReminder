package com.kotlity.core

import com.kotlity.core.util.DispatcherHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule: TestWatcher(), KoinComponent {

    private val testDispatcherHandler by inject<DispatcherHandler>()

    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcherHandler.main)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}