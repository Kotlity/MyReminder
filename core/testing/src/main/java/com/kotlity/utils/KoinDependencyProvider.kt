package com.kotlity.utils

import org.junit.Rule
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

abstract class KoinDependencyProvider(modules: List<Module>): KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(modules = modules)
    }
}