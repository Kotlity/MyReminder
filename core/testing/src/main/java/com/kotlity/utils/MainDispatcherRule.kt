package com.kotlity.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(private val coroutineDispatcher: CoroutineDispatcher = StandardTestDispatcher()): TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(coroutineDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}