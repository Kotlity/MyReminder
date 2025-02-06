package com.kotlity.utils

import android.os.Build
import androidx.test.rule.GrantPermissionRule.*
import org.junit.rules.TestRule

abstract class GrantPermissionRule(
    private val sdkVersion: Int,
    private val permission: String
): TestRule by if (Build.VERSION.SDK_INT >= sdkVersion) grant(permission) else grant()