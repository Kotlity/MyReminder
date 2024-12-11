package com.kotlity.core.presentation.util

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScreenDimensions(val width: Dp = 0.dp, val height: Dp = 0.dp)

val LocalScreenSize = staticCompositionLocalOf { ScreenDimensions() }