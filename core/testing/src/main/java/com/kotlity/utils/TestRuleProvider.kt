package com.kotlity.utils

import io.mockk.junit4.MockKRule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Rule

interface TestRuleProvider {

    val coroutineDispatcher: CoroutineDispatcher
        get() = StandardTestDispatcher()

    @get:Rule
    val mockKRule: MockKRule
        get() = MockKRule(this)

    @get:Rule
    val mainDispatcherRule: MainDispatcherRule
        get() = MainDispatcherRule(coroutineDispatcher = coroutineDispatcher)
}