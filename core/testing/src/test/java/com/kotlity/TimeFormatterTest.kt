package com.kotlity

import com.google.common.truth.Truth.assertThat
import com.kotlity.di.testTimeFormatterModule
import com.kotlity.utils.KoinDependencyProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.test.inject

class TimeFormatterTest: KoinDependencyProvider(modules = listOf(testTimeFormatterModule)) {

    private val timeFormatter by inject<TimeFormatter>()

    @Test
    fun `initially is24HourFormat equals to true`() = runTest {
        val initialIs24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(initialIs24HourFormat).isTrue()
    }

    @Test
    fun `updates is24HourFormat to false returns false`() = runTest {
        timeFormatter.is24HourFormatChanged(update = false)

        val updatedIs24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(updatedIs24HourFormat).isFalse()
    }

    @Test
    fun `updates is24HourFormat to false then updates to true and returns true`() = runTest {
        timeFormatter.is24HourFormatChanged(update = false)

        val updatedIs24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(updatedIs24HourFormat).isFalse()

        timeFormatter.is24HourFormatChanged(true)

        val finalIs24HourFormat = timeFormatter.is24HourFormat.first()
        assertThat(finalIs24HourFormat).isTrue()
    }
}