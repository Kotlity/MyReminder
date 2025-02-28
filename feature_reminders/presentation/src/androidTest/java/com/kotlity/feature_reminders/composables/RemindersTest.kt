package com.kotlity.feature_reminders.composables

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.feature_reminders.utils.RemindersUtil.lastReminderItem
import com.kotlity.feature_reminders.utils.RemindersUtil.mockReminders
import com.kotlity.utils.ComposeTestRuleProvider
import org.junit.Test

class RemindersTest: ComposeTestRuleProvider() {

    private val reminders: SemanticsNodeInteraction by lazy { composeTestRule.onNode(hasScrollToNodeAction()) }

    @Test
    fun onReminders_cantScroll_whenUserActionsEnabledSetToFalse() {
        composeTestRule.setContent {
            MyReminderTheme {
                Reminders(
                    userActionsEnabled = false,
                    reminders = mockReminders,
                    onReminderClick = { _,_ -> }
                )
            }
        }

        reminders.assertDoesNotExist()

        composeTestRule
            .onRoot()
            .performTouchInput { swipeDown() }

        onNodeWithText(lastReminderItem.title).assertDoesNotExist()
    }

    @Test
    fun onReminders_scrollToLastItem_showsLastItem() {
        composeTestRule.setContent {
            MyReminderTheme {
                Reminders(
                    reminders = mockReminders,
                    onReminderClick = { _,_ -> }
                )
            }
        }

        reminders
            .assertIsDisplayed()
            .performScrollToNode(hasText(lastReminderItem.title))

        onNodeWithText(lastReminderItem.title).assertIsDisplayed()
    }
}