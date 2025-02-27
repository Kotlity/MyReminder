package com.kotlity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.*

internal fun grantPostNotificationsPermission(
    context: Context,
    uiDevice: UiDevice
) {
    val appIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(appIntent)

    val settingsSelector = By.pkg("com.android.settings")

    uiDevice.wait(Until.hasObject(settingsSelector), 5000)

    assertThat(uiDevice.hasObject(settingsSelector)).isTrue()

    val notificationsButton = uiDevice.findObject(By.text("Notifications"))

    assertThat(notificationsButton).isNotNull()

    uiDevice.performActionAndWait({ notificationsButton.click() }, Until.newWindow(), 5000)

    val notificationsSwitcher = uiDevice.findObject(By.clazz("android.widget.Switch"))

    assertThat(notificationsSwitcher.isChecked).isFalse()

    notificationsSwitcher.click()

    uiDevice.waitForIdle(5000)

    assertThat(notificationsSwitcher.isChecked).isTrue()
}