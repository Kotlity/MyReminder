package com.kotlity.feature_reminder_editor.mappers

import android.Manifest
import com.kotlity.permissions.Permission

internal fun Permission.mapToString(): String {
    return when(this) {
        Permission.NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
    }
}

internal fun String.toPermission(): Permission {
    return when(this) {
        Manifest.permission.POST_NOTIFICATIONS -> Permission.NOTIFICATIONS
        else -> throw Exception("Unsupported permission type received")
    }
}