package com.kotlity.feature_reminders.composables

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onAllNodesWithText
import com.kotlity.core.resources.R.*
import com.kotlity.utils.ComposeTestRuleProvider
import org.junit.Test

class AddTaskSectionTest: ComposeTestRuleProvider() {

    private val addIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.addTaskIconDescription)) }
    private val addTask: SemanticsNodeInteraction by lazy { onNodeWithText(getString(string.addTask)) }

    @Test
    fun onAddTaskLabelIsNotVisible() {
        composeTestRule.setContent {
            AddTaskSection(
                isAddTaskLabelVisible = false,
                isAddTaskClickable = true,
                onIconPositioned = {},
                onAddTaskClick = {}
            )
        }

        addIcon
            .assertIsDisplayed()
            .assertIsEnabled()

        addTask.assertDoesNotExist()
    }

    @Test
    fun initially_AddTaskLabelIsNotVisible_afterSomeTime_AddTaskLabelVisible() {
        var isAddTaskLabelVisible by mutableStateOf(false)

        composeTestRule.setContent {
            AddTaskSection(
                isAddTaskLabelVisible = isAddTaskLabelVisible,
                isAddTaskClickable = true,
                onIconPositioned = {},
                onAddTaskClick = {}
            )
        }

        isAddTaskLabelVisible = true

        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithText(getString(string.addTask))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        addTask
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}