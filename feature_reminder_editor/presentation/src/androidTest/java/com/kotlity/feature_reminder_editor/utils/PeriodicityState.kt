package com.kotlity.feature_reminder_editor.utils

import com.kotlity.core.Periodicity

internal data class PeriodicityState(
    val periodicity: Periodicity = Periodicity.ONCE,
    val isExpanded: Boolean = false
)
