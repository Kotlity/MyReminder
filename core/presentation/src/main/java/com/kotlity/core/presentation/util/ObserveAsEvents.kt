package com.kotlity.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun <T> ObserveAsEvents(
    vararg events: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    minActiveStateToObserve: Lifecycle.State = Lifecycle.State.STARTED,
    onObserve: suspend (T) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = lifecycleOwner.lifecycle, key1, key2) {
        lifecycleOwner.repeatOnLifecycle(minActiveStateToObserve) {
            withContext(Dispatchers.Main.immediate) {
                events.forEach { event ->
                    event.collect {
                        launch {
                            onObserve(it)
                        }
                    }
                }
            }
        }
    }
}