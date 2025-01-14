package com.kotlity.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.module.Module
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
abstract class AndroidKoinDependencyProvider(modules: List<Module>): KoinTest {

    @get:Rule
    val koinTestRule = CustomKoinTestRule(modules = modules)
}