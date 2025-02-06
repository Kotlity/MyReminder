package com.kotlity.utils

import android.Manifest
import android.os.Build

class GrantPostNotificationsPermissionRule: GrantPermissionRule(sdkVersion = Build.VERSION_CODES.TIRAMISU, permission = Manifest.permission.POST_NOTIFICATIONS)