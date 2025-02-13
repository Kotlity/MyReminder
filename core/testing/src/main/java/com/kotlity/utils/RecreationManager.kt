package com.kotlity.utils

import androidx.compose.runtime.Composable

interface RecreationManager {

    fun setContent(content: @Composable () -> Unit)

    fun recreateWith(action: () -> Unit)

}