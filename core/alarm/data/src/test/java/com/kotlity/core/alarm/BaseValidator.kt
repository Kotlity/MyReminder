package com.kotlity.core.alarm

import io.mockk.junit4.MockKRule
import org.junit.Rule

abstract class BaseValidator {

    @get:Rule
    val mockKRule = MockKRule(this)
}