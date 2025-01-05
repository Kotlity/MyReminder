package com.kotlity.feature_reminders.mappers

import androidx.compose.ui.unit.IntOffset

internal fun Pair<Int, Int>.toIntOffset(): IntOffset {
    return IntOffset(x = first, y = second)
}