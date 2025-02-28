package com.kotlity.feature_reminders.composables

import androidx.annotation.StringRes
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.primary
import com.kotlity.core.ui.theme.tertiaryContainer
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.resources.R.*

private const val NUMBER_OF_LINES = 8
private const val ANIMATION_DURATION = 3000

@Composable
internal fun LoadingIndicator(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = dimen._10dp)),
    border: BorderStroke? = null,
    loadingIndicatorSize: Dp = dimensionResource(id = dimen._48dp),
    lineWidth: Float = with(LocalDensity.current) { dimensionResource(id = dimen._5dp).toPx() },
    lineHeight: Float = with(LocalDensity.current) { dimensionResource(id = dimen._15dp).toPx() },
    baseLineColor: Color = primary,
    progressLineColor: Color = tertiaryContainer,
    @StringRes loadingIndicatorTestTag: Int = string.loadingIndicatorTestTag
) {

    val infiniteTransition = rememberInfiniteTransition(label = stringResource(id = string.loadingIndicatorLabel))

    val colorConverter = TwoWayConverter<Color, AnimationVector4D>(
        convertToVector = { color ->
            AnimationVector4D(v1 = color.red, v2 = color.green, v3 = color.blue, v4 = color.alpha)
        },
        convertFromVector = { animationVector4D ->
            Color(red = animationVector4D.v1, green = animationVector4D.v2, blue = animationVector4D.v3, alpha = animationVector4D.v4)
        }
    )

    val colorLineAnimationValues = infiniteTransition.animateLoadingIndicatorValues(
        initialValue = baseLineColor,
        typeConverter = colorConverter,
        keyFramesBuilder = {
            progressLineColor at ANIMATION_DURATION / NUMBER_OF_LINES / 2 using LinearEasing
            baseLineColor at ANIMATION_DURATION / NUMBER_OF_LINES using LinearEasing
        },
        label = stringResource(id = string.loadingIndicatorColorLabel)
    )

    val heightLineAnimationValues = infiniteTransition.animateLoadingIndicatorValues(
        initialValue = 1f,
        typeConverter = Float.VectorConverter,
        keyFramesBuilder = {
            1.5f at ANIMATION_DURATION / NUMBER_OF_LINES / 2 using LinearEasing
            1f at ANIMATION_DURATION / NUMBER_OF_LINES using LinearEasing
        },
        label = stringResource(id = string.loadingIndicatorHeightLabel)
    )

    Card(
        modifier = modifier.testTag(stringResource(id = loadingIndicatorTestTag)),
        shape = shape,
        elevation = elevation,
        border = border
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(loadingIndicatorSize)) {
                repeat(NUMBER_OF_LINES) { index ->
                    rotate(degrees = index * 45f) {
                        val startOffset = Offset(x = size.width / 2, y = size.height / 4)
                        val endOffset = Offset(x = size.width / 2, y = size.height / 4 - lineHeight * heightLineAnimationValues[index].value)

                        drawLine(
                            color = colorLineAnimationValues[index].value,
                            start = startOffset,
                            end = endOffset,
                            strokeWidth = lineWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun <T, V: AnimationVector> InfiniteTransition.animateLoadingIndicatorValues(
    initialValue: T,
    targetValue: T = initialValue,
    typeConverter: TwoWayConverter<T, V>,
    durationMillis: Int = ANIMATION_DURATION / 2,
    keyFramesBuilder: KeyframesSpec.KeyframesSpecConfig<T>.() -> Unit,
    label: String
): List<State<T>> {
    return (0 until NUMBER_OF_LINES).map { index ->
        animateValue(
            initialValue = initialValue,
            targetValue = targetValue,
            typeConverter = typeConverter,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    this.durationMillis = durationMillis
                    keyFramesBuilder()
                },
                initialStartOffset = StartOffset(offsetMillis = ANIMATION_DURATION / NUMBER_OF_LINES / 2 * index)
            ),
            label = label
        )
    }
}

@PreviewAnnotation
@Composable
private fun LoadingWheelPreview() {
    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator(
                modifier = Modifier.size(dimensionResource(id = dimen._86dp))
            )
        }
    }
}