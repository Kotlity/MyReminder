package com.kotlity.feature_reminders.utils

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.kotlity.utils.RecreationManager

internal class ActivityRecreationManager<A : ComponentActivity>(
    private val androidComposeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
): RecreationManager {

    private var content: (@Composable () -> Unit)? = null

    override fun setContent(content: @Composable () -> Unit) {
        this.content = content
        androidComposeTestRule.setContent(content)
    }

    override fun recreateWith(action: () -> Unit) {
        action()

        val composeTest = androidComposeTestRule::class.java.getDeclaredField("composeTest").also {
            it.isAccessible = true
        }.get(androidComposeTestRule)
        composeTest::class.java.getDeclaredField("disposeContentHook").also {
            it.isAccessible = true
        }.set(composeTest, null)
        content?.let { androidComposeTestRule.setContent(it) }
    }
}