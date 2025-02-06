package com.kotlity.utils

import android.Manifest
import android.os.Build

class GrantScheduleExactAlarmPermissionRule: GrantPermissionRule(sdkVersion = Build.VERSION_CODES.S, permission = Manifest.permission.SCHEDULE_EXACT_ALARM)