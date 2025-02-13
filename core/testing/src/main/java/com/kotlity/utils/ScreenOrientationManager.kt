package com.kotlity.utils

import androidx.test.espresso.device.action.ScreenOrientation

interface ScreenOrientationManager {

    val orientation: Int

    fun changeScreenOrientation(orientation: ScreenOrientation)
}