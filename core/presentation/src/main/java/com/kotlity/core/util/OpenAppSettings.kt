package com.kotlity.core.util

import android.app.Activity
import android.content.Intent
import android.net.Uri

fun Activity.openAppSettings(settingsPath: String) {
    Intent(settingsPath, Uri.fromParts("package", packageName, null)).also(::startActivity)
}