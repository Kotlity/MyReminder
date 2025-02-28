package com.kotlity.feature_reminders.composables

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.Periodicity
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.mapToString
import com.kotlity.feature_reminders.mappers.toDisplayableReminderTime
import com.kotlity.feature_reminders.models.ReminderUi
import com.kotlity.feature_reminders.utils.DateUtil
import com.kotlity.utils.ComposeTestRuleProvider
import org.junit.Test

class ReminderItemTest: ComposeTestRuleProvider() {

    private val time: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.reminderTimeTextTestTag)) }
    private val date: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.reminderDateTextTestTag)) }
    private val periodicity: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.reminderPeriodicityTextTestTag)) }
    private val expandIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.expandIconDescription)) }

    private val dateUtil = DateUtil()

    private val reminderTime = dateUtil.timeInMillis.toDisplayableReminderTime(is24HourFormat = true)
    private val reminderUi = ReminderUi(
        id = 0,
        title = "Tugas PPL",
        reminderTime = reminderTime,
        periodicity = Periodicity.DAILY
    )

    @Test
    fun onReminderItem_allNecessaryInformation_isDisplayedCorrectly() {
        val expectedTime = dateUtil.expectedTime
        val expectedDate = dateUtil.expectedDate
        val expectedPeriodicity = reminderUi.periodicity.mapToString(context)

        composeTestRule.setContent {
            MyReminderTheme {
                ReminderItem(
                    reminderUi = reminderUi,
                    onReminderExpandIconClick = {}
                )
            }
        }

        onNodeWithText("Tugas PPL")
            .assertIsDisplayed()
            .assertTextEquals(reminderUi.title)

        time
            .assertIsDisplayed()
            .assertTextEquals(expectedTime)

        date
            .assertIsDisplayed()
            .assertTextEquals(expectedDate)

        periodicity
            .assertIsDisplayed()
            .assertTextEquals(expectedPeriodicity)

        expandIcon
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun onReminderItem_onExpandIconClick_worksCorrectly() {
        var isExpandIconClicked = false

        composeTestRule.setContent {
            MyReminderTheme {
                ReminderItem(
                    reminderUi = reminderUi,
                    onReminderExpandIconClick = {
                        isExpandIconClicked = true
                    }
                )
            }
        }

        assertThat(isExpandIconClicked).isFalse()

        expandIcon.performClick()

        assertThat(isExpandIconClicked).isTrue()
    }

}