package com.kotlity.utils

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.device.DeviceInteraction.Companion.setScreenOrientation
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

open class AndroidComposeTestRuleProvider<T: ComponentActivity>(
    activityClass: Class<T>
): ResourceOperator, TestNodeFinder, ScreenOrientationManager {

    @get:Rule
    val androidComposeTestRule = createAndroidComposeRule(activityClass = activityClass)

    @get:Rule
    val screenOrientationRule = ScreenOrientationRule(ScreenOrientation.PORTRAIT)

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    override fun getString(id: Int) = context.getString(id)

    override val orientation: Int
        get() = androidComposeTestRule.activity.resources.configuration.orientation

    override fun changeScreenOrientation(orientation: ScreenOrientation) {
        onDevice().setScreenOrientation(orientation)
    }

    override fun onNodeWithContentDescription(
        description: String,
        substring: Boolean,
        ignoreCase: Boolean,
        useUnmergedTree: Boolean
    ): SemanticsNodeInteraction {
        return androidComposeTestRule.onNodeWithContentDescription(
            label = description,
            substring = substring,
            ignoreCase = ignoreCase,
            useUnmergedTree = useUnmergedTree
        )
    }

    override fun onNodeWithTestTag(tag: String, useUnmergedTree: Boolean): SemanticsNodeInteraction {
        return androidComposeTestRule.onNodeWithTag(
            testTag = tag,
            useUnmergedTree = useUnmergedTree
        )
    }

    override fun onNodeWithText(
        text: String,
        substring: Boolean,
        ignoreCase: Boolean,
        useUnmergedTree: Boolean
    ): SemanticsNodeInteraction {
        return androidComposeTestRule.onNodeWithText(
            text = text,
            substring = substring,
            ignoreCase = ignoreCase,
            useUnmergedTree = useUnmergedTree
        )
    }
}