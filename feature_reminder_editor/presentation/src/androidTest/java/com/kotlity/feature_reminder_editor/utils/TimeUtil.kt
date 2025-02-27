package com.kotlity.feature_reminder_editor.utils

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

internal class TimeUtil<A: ComponentActivity>(
    private val androidComposeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {

    private val zoneIdUTC = ZoneId.of("Z")

    val currentTime: LocalTime = LocalTime.now(zoneIdUTC)

    fun getTimePickerHourNode(
        localTime: LocalTime = currentTime
    ): SemanticsNodeInteraction = androidComposeTestRule.onNodeWithContentDescription("${localTime.hour} hours")

    fun getTimePickerMinuteNode(
        localTime: LocalTime = currentTime
    ): SemanticsNodeInteraction {
        val appropriateMinute = localTime.minute - localTime.minute % 5
        return androidComposeTestRule.onNodeWithContentDescription("$appropriateMinute minutes")
    }
}