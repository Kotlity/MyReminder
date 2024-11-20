package com.kotlity.core.alarm.data

import io.mockk.junit4.MockKRule
import org.junit.Rule

abstract class BaseValidator {

    @get:Rule
    val mockKRule = MockKRule(this)
}