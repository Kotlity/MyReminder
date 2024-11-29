package com.kotlity.core.presentation.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnimatedVisibilityContent(
    modifier: Modifier = Modifier,
    condition: Boolean,
    enterTransition: EnterTransition = fadeIn() + expandIn(),
    exitTransition: ExitTransition = shrinkOut() + fadeOut(),
    content: @Composable (AnimatedVisibilityScope.(Modifier) -> Unit)
) {

    AnimatedVisibility(
        visible = condition,
        enter = enterTransition,
        exit = exitTransition
    ) {
        content(modifier)
    }
}