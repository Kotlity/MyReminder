package com.kotlity.utils

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

abstract class ComposeTestRuleProvider: ResourceOperator, TestNodeFinder {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    override fun getString(id: Int) = context.getString(id)

    override fun onNodeWithContentDescription(
        description: String,
        substring: Boolean,
        ignoreCase: Boolean,
        useUnmergedTree: Boolean
    ): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithContentDescription(
            label = description,
            substring = substring,
            ignoreCase = ignoreCase,
            useUnmergedTree = useUnmergedTree
        )
    }

    override fun onNodeWithTestTag(tag: String, useUnmergedTree: Boolean): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithTag(
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
        return composeTestRule.onNodeWithText(
            text = text,
            substring = substring,
            ignoreCase = ignoreCase,
            useUnmergedTree = useUnmergedTree
        )
    }
}