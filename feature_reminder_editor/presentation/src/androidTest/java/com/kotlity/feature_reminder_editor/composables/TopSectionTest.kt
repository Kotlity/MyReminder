package com.kotlity.feature_reminder_editor.composables

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.utils.ComposeTestRuleProvider
import org.junit.Test

class TopSectionTest: ComposeTestRuleProvider() {

    private val backIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.backIconButtonDescription)) }
    private val doneIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.doneIconButtonDescription)) }

    @Test
    fun backButton_clickPerformed() {
        var isBackButtonClicked = false
        composeTestRule.setContent {
            MyReminderTheme {
                TopSection(
                    isDoneButtonEnabled = false,
                    onBackClick = { isBackButtonClicked = true },
                    onDoneClick = {}
                )
            }
        }

        backIcon
            .assertHasClickAction()
            .performClick()

        assertThat(isBackButtonClicked).isTrue()
    }

    @Test
    fun initial_isDoneButtonEnabledIsFalse() {
        composeTestRule.setContent {
            MyReminderTheme {
                TopSection(
                    isDoneButtonEnabled = false,
                    onBackClick = {},
                    onDoneClick = {}
                )
            }
        }

        backIcon.assertIsEnabled()

        doneIcon.assertIsNotEnabled()
    }

    @Test
    fun finally_isDoneButtonEnabledIsTrue() {
        var isDoneButtonClicked = false
        composeTestRule.setContent {
            MyReminderTheme {
                TopSection(
                    isDoneButtonEnabled = true,
                    onBackClick = {},
                    onDoneClick = { isDoneButtonClicked = true }
                )
            }
        }

        doneIcon
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        assertThat(isDoneButtonClicked).isTrue()
    }

}