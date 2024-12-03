package com.kotlity.feature_reminders.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun Circle(
    modifier: Modifier = Modifier,
    color: Color
) {

    Box(
        modifier = modifier
            .drawBehind {
                drawCircle(
                    color = color,
                    style = Stroke(size.width / 2.5f)
                )
            }
    )
}