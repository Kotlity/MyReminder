package com.kotlity.core.presentation.util

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DefaultSnackbarHost(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(snackbarData = it) }
) {

    SnackbarHost(
        modifier = modifier,
        hostState = snackbarHostState,
        snackbar = snackbar
    )

}