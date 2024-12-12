package com.kotlity.feature_reminders.presentation.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kotlity.core.presentation.ui.theme.MyReminderTheme
import com.kotlity.core.presentation.ui.theme.primary
import com.kotlity.core.presentation.ui.theme.tertiaryContainer
import com.kotlity.core.presentation.util.PreviewAnnotation
import com.kotlity.core.resources.R.*

private const val NUMBER_OF_LINES = 8
private const val ANIMATION_DURATION = 1500

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    lineWidth: Float = with(LocalDensity.current) { dimensionResource(id = dimen._2dp).toPx() },
    baseLineColor: Color = primary,
    progressLineColor: Color = tertiaryContainer
) {

    val infiniteTransition = rememberInfiniteTransition(label = stringResource(id = string.loadingIndicatorLabel))
    val colorLineAnimationValues = (0 until NUMBER_OF_LINES).map {
        infiniteTransition.animateColor(
            initialValue = baseLineColor,
            targetValue = progressLineColor,
            animationSpec = infiniteRepeatable(animation = tween(durationMillis = ANIMATION_DURATION, easing = LinearEasing)),
            label = stringResource(id = string.loadingIndicatorColorLabel)
        )
    }

    Canvas(modifier = modifier) {
        repeat(NUMBER_OF_LINES) { index ->
            rotate(degrees = index * 45f) {
                drawLine(
                    color = colorLineAnimationValues[index].value,
                    start = Offset(x = size.width / 2, y = size.height / 4),
                    end = Offset(x = size.width / 2, y = size.height / 4),
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@PreviewAnnotation
@Composable
fun LoadingWheelPreview() {
    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator(modifier = Modifier.size(48.dp))
        }
    }
}