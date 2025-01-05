package com.kotlity.core.util

import com.kotlity.core.util.Constants.WHILE_SUBSCRIBED_DELAY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

inline fun <reified T> Flow<T>.toStateFlow(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(stopTimeoutMillis = WHILE_SUBSCRIBED_DELAY),
    initialValue: T
) = stateIn(
    scope = scope,
    started = started,
    initialValue = initialValue
)