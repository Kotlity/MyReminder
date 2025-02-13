package com.kotlity.feature_reminder_editor.composables

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.espresso.device.action.ScreenOrientation
import com.google.common.truth.Truth.assertThat
import com.kotlity.core.Periodicity
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.feature_reminder_editor.composables.periodicity.PeriodicitySection
import com.kotlity.feature_reminder_editor.utils.ActivityRecreationManager
import com.kotlity.feature_reminder_editor.utils.PeriodicityState
import com.kotlity.utils.AndroidComposeTestRuleProvider
import org.junit.Before
import org.junit.Test

class PeriodicitySectionTest: AndroidComposeTestRuleProvider<ComponentActivity>(ComponentActivity::class.java) {

    private lateinit var activityRecreationManager: ActivityRecreationManager<ComponentActivity>

    private val periodicityTitle: SemanticsNodeInteraction by lazy { onNodeWithText(periodicityTitleText) }
    private val periodicityIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.periodicityIconDescription)) }
    private val selectedPeriodicityItem: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.selectedPeriodicityItemTestTag), useUnmergedTree = true) }
    private val selectedPeriodicityItemTextNode: SemanticsNodeInteraction by lazy { selectedPeriodicityItem.onChildren().onFirst() }
    private val expandedPeriodicityIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.periodicityArrowUpDescription)) }
    private val abbreviatedPeriodicityIcon: SemanticsNodeInteraction by lazy { onNodeWithContentDescription(getString(string.periodicityArrowDownDescription)) }
    private val periodicityMenu: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.periodicityListTestTag)) }

    private val periodicityTitleText = getString(string.repeat)
    private val once = getString(string.once)
    private val daily = getString(string.daily)
    private val monToFri = getString(string.monToFri)

    private var periodicityState by mutableStateOf(PeriodicityState())

    private fun onSelectedPeriodicityClick() {
        periodicityState = periodicityState.copy(isExpanded = !periodicityState.isExpanded)
    }

    private fun onPeriodicityItemClick(periodicity: Periodicity) {
        periodicityState = periodicityState.copy(periodicity = periodicity)
    }

    @Before
    fun setup() {
        periodicityState = PeriodicityState()
        activityRecreationManager = ActivityRecreationManager(androidComposeTestRule)
    }

    @Test
    fun initial_periodicitySectionState() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                PeriodicitySection(
                    periodicity = periodicityState.periodicity,
                    isExpanded = periodicityState.isExpanded,
                    onSelectedItemClick = {},
                    onPeriodicityItemClick = {}
                )
            }
        }

        periodicityTitle
            .assertIsDisplayed()
            .assertTextEquals(periodicityTitleText)

        periodicityIcon.assertIsDisplayed()

        selectedPeriodicityItem
            .assertIsDisplayed()
            .assertHasClickAction()

        selectedPeriodicityItemTextNode
            .assertIsDisplayed()
            .assertTextEquals(once)

        expandedPeriodicityIcon.assertDoesNotExist()

        abbreviatedPeriodicityIcon.assertIsDisplayed()

        periodicityMenu.assertDoesNotExist()
    }

    @Test
    fun onSelectedPeriodicityClicked_expandsPeriodicityMenu() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                PeriodicitySection(
                    periodicity = periodicityState.periodicity,
                    isExpanded = periodicityState.isExpanded,
                    onSelectedItemClick = { onSelectedPeriodicityClick() },
                    onPeriodicityItemClick = {}
                )
            }
        }

        selectedPeriodicityItem.performClick()

        assertThat(periodicityState.isExpanded).isTrue()

        expandedPeriodicityIcon.assertIsDisplayed()

        abbreviatedPeriodicityIcon.assertDoesNotExist()

        periodicityMenu.assertIsDisplayed()

        periodicityMenu.assert(hasAnyAncestor(isPopup()))

        periodicityMenu
            .onChildren()
            .assertCountEquals(3)
    }

    @Test
    fun onSelectedPeriodicityClicked_expandsPeriodicityMenu_appWentToBackgroundAndResumed_keepDisplayingPeriodicityMenu() {
        val testLifecycleOwner = TestLifecycleOwner()
        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.STARTED)

        androidComposeTestRule.setContent {
            MyReminderTheme {
                PeriodicitySection(
                    periodicity = periodicityState.periodicity,
                    isExpanded = periodicityState.isExpanded,
                    onSelectedItemClick = { onSelectedPeriodicityClick() },
                    onPeriodicityItemClick = {}
                )
            }
        }

        selectedPeriodicityItem.performClick()

        periodicityMenu.assertIsDisplayed()

        periodicityMenu
            .onChildren()
            .assertCountEquals(3)

        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.CREATED)

        testLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        assertThat(testLifecycleOwner.currentState).isEqualTo(Lifecycle.State.RESUMED)

        periodicityMenu.assertIsDisplayed()

        periodicityMenu
            .onChildren()
            .assertCountEquals(3)
    }

    @Test
    fun onSelectedPeriodicityClicked_expandsPeriodicityMenu_changingOrientation_keepDisplayingPeriodicityMenu() {
        activityRecreationManager.setContent {
            MyReminderTheme {
                PeriodicitySection(
                    periodicity = periodicityState.periodicity,
                    isExpanded = periodicityState.isExpanded,
                    onSelectedItemClick = { onSelectedPeriodicityClick() },
                    onPeriodicityItemClick = {}
                )
            }
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_PORTRAIT)

        selectedPeriodicityItem.performClick()

        activityRecreationManager.recreateWith { changeScreenOrientation(ScreenOrientation.LANDSCAPE) }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        periodicityMenu.assertIsDisplayed()

        periodicityMenu.assert(hasAnyAncestor(isPopup()))

        periodicityMenu
            .onChildren()
            .assertCountEquals(3)
    }

    @Test
    fun onPeriodicityItemClicked_updatesSelectedPeriodicityItem() {
        androidComposeTestRule.setContent {
            MyReminderTheme {
                PeriodicitySection(
                    periodicity = periodicityState.periodicity,
                    isExpanded = periodicityState.isExpanded,
                    onSelectedItemClick = { onSelectedPeriodicityClick() },
                    onPeriodicityItemClick = { periodicity ->
                        onPeriodicityItemClick(periodicity = periodicity)
                    }
                )
            }
        }

        selectedPeriodicityItem.performClick()

        periodicityMenu
            .onChildren()
            .onLast()
            .assertHasClickAction()
            .performClick()

        periodicityMenu.assertDoesNotExist()

        selectedPeriodicityItemTextNode.assertTextEquals(monToFri)

        assertThat(periodicityState.periodicity).isEqualTo(Periodicity.WEEKDAYS)
    }

    @Test
    fun updatedSelectedPeriodicityItem_changedScreenOrientation_keepDisplayingUpdatedPeriodicityItem() {
        changeScreenOrientation(ScreenOrientation.LANDSCAPE)
        activityRecreationManager.setContent {
            MyReminderTheme {
                PeriodicitySection(
                    periodicity = periodicityState.periodicity,
                    isExpanded = periodicityState.isExpanded,
                    onSelectedItemClick = { onSelectedPeriodicityClick() },
                    onPeriodicityItemClick = { periodicity ->
                        onPeriodicityItemClick(periodicity = periodicity)
                    }
                )
            }
        }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE)

        selectedPeriodicityItem.performClick()

        periodicityMenu
            .onChildren()
            .filterToOne(hasTextExactly(daily))
            .performClick()

        periodicityMenu.assertDoesNotExist()

        selectedPeriodicityItemTextNode.assertTextEquals(daily)

        assertThat(periodicityState.periodicity).isEqualTo(Periodicity.DAILY)

        activityRecreationManager.recreateWith { changeScreenOrientation(ScreenOrientation.PORTRAIT) }

        assertThat(orientation).isEqualTo(Configuration.ORIENTATION_PORTRAIT)

        periodicityMenu.assertDoesNotExist()

        selectedPeriodicityItemTextNode.assertTextEquals(daily)
    }
}