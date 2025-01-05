package com.kotlity.core.util

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherHandler {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}