package com.kotlity

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.kotlity.core.composables.NotificationsPermissionTextProvider
import com.kotlity.core.composables.PermissionDialog
import com.kotlity.core.resources.R.*
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.utils.ComposeTestRuleProvider
import org.junit.Test

class PermissionDialogTest: ComposeTestRuleProvider() {

    private val uiDevice: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val notificationsPermissionTextProvider = NotificationsPermissionTextProvider()

    private val confirmButton: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.confirmButtonTestTag)) }
    private val dismissButton: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.dismissButtonTestTag)) }
    private val title: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.titleTestTag)) }
    private val text: SemanticsNodeInteraction by lazy { onNodeWithTestTag(getString(string.textTestTag)) }

    private val okText = getString(string.ok)
    private val grantPermissionText = getString(string.grantPermission)
    private val dismissText = getString(string.dismiss)
    private val titleText = getString(string.permissionTitle)
    private val notificationsPermissionText = getString(string.notificationsPermissionText)
    private val notificationsPermissionPermanentlyDeclinedText = getString(string.notificationsPermissionPermanentlyDeclinedText)

    @Test
    fun permissionDialogShowsAtTheFirstTime() {
        composeTestRule.setContent {
            MyReminderTheme {
                PermissionDialog(
                    permissionTextProvider = notificationsPermissionTextProvider,
                    isPermanentlyDeclined = false,
                    onDismissClick = {},
                    onOkClick = {},
                    onGoToAppSettingsClick = {}
                )
            }
        }

        title
            .assertIsDisplayed()
            .assertTextEquals(titleText)

        text
            .assertIsDisplayed()
            .assertTextEquals(notificationsPermissionText)

        dismissButton
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertTextEquals(dismissText)

        confirmButton
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertTextEquals(okText)
    }

    @Test
    fun permissionDialogInPermanentlyDeclinedState() {
        composeTestRule.setContent {
            MyReminderTheme {
                PermissionDialog(
                    permissionTextProvider = notificationsPermissionTextProvider,
                    isPermanentlyDeclined = true,
                    onDismissClick = {},
                    onOkClick = {},
                    onGoToAppSettingsClick = {}
                )
            }
        }

        text.assertTextEquals(notificationsPermissionPermanentlyDeclinedText)

        confirmButton.assertTextEquals(grantPermissionText)
    }

    @Test
    fun notificationsPermissionIsPermanentlyDeclined_clickOnConfirmButton_goToAppSettingsAndGrantPermission() {
        composeTestRule.setContent {
            MyReminderTheme {
                PermissionDialog(
                    permissionTextProvider = notificationsPermissionTextProvider,
                    isPermanentlyDeclined = true,
                    onDismissClick = {},
                    onOkClick = {},
                    onGoToAppSettingsClick = { grantPostNotificationsPermission(context, uiDevice) }
                )
            }
        }

        confirmButton.performClick()
    }
}