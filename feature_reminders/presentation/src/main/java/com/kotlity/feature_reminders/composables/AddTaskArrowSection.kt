
package com.kotlity.feature_reminders.composables

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.darkBlack
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.resources.R.*
import com.kotlity.core.ResourcesConstant._1200
import com.kotlity.core.ResourcesConstant._1f
import com.kotlity.core.ResourcesConstant._300
import com.kotlity.core.ResourcesConstant._900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddTaskArrowSection(
    modifier: Modifier = Modifier,
    arrowWidth: Float = dimensionResource(id = dimen._5dp).toPx(),
    text: String = stringResource(id = string.addYourTask),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    @StringRes addTaskArrowSectionTestTag: Int = string.addTaskArrowSectionTestTag
) {
    val arrowAnimation = remember {
        Animatable(0f)
    }
    val rightArrowPointerAnimation = remember {
        Animatable(0f)
    }
    val textAlphaAnimation = remember {
        Animatable(0f)
    }

    val arrowPathMeasure = remember {
        PathMeasure()
    }
    val rightArrowPointerPathMeasure = remember {
        PathMeasure()
    }

    val textMeasurer = rememberTextMeasurer()

    val textXTranslationInPx = dimensionResource(id = dimen._30dp).toPx()
    val textYTranslationInPx = dimensionResource(id = dimen.minus12dp).toPx()

    LaunchedEffect(key1 = Unit) {
        launch {
            arrowAnimation.animateTo(_1f, tween(durationMillis = _1200, easing = FastOutLinearInEasing))
            rightArrowPointerAnimation.animateTo(_1f, tween(durationMillis = _300))
        }
        launch {
            delay(_900.toLong())
            textAlphaAnimation.animateTo(_1f, tween(_900))
        }
    }

    Canvas(modifier = modifier.testTag(stringResource(id = addTaskArrowSectionTestTag))) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val arrowPath = arrowPath(canvasWidth, canvasHeight)
        val rightArrowPointerPath = Path().apply {
            moveTo(canvasWidth / 2.7f, 10f)
            cubicTo(
                canvasWidth / 2.7f, 10f,
                canvasWidth / 2f, 40f,
                canvasWidth / 1.55f, 50f
            )
        }

        val animatedArrowPath = arrowPathMeasure.getAnimatedPath(arrowPath, arrowAnimation.value)
        drawPath(
            animatedArrowPath,
            color = darkBlack,
            style = Stroke(width = arrowWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        if (rightArrowPointerAnimation.value > 0f) {
            val animatedRightArrowPointerPath = rightArrowPointerPathMeasure.getAnimatedPath(rightArrowPointerPath, rightArrowPointerAnimation.value)

            drawPath(
                animatedRightArrowPointerPath,
                color = darkBlack,
                style = Stroke(width = arrowWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        val textOffset = Offset(x = canvasWidth / 1.8f, y = canvasHeight / 1.4f)
        drawArrowText(
            textMeasurer = textMeasurer,
            offset = textOffset,
            text = text,
            style = textStyle.copy(color = darkBlack.copy(alpha = textAlphaAnimation.value)),
            xTranslation = textXTranslationInPx,
            yTranslation = textYTranslationInPx
        )
    }
}

@Composable
private fun Dp.toPx(): Float {
    val density = LocalDensity.current.density
    return value * density
}

private fun arrowPath(canvasWidth: Float, canvasHeight: Float) = Path().apply {
    moveTo(canvasWidth, canvasHeight)
    cubicTo(
        canvasWidth, canvasHeight,
        canvasWidth / 1.3f, canvasHeight - 130f,
        canvasWidth / 1.8f, canvasHeight / 1.4f
    )
    cubicTo(
        canvasWidth / 1.8f, canvasHeight / 1.4f,
        canvasWidth / 2.1f, canvasHeight / 1.55f,
        30f, canvasHeight / 1.7f
    )
    cubicTo(
        0f, canvasHeight / 1.7f,
        0f, canvasHeight / 1.5f,
        20f, canvasHeight / 1.4f
    )
    cubicTo(
        20f, canvasHeight / 1.4f,
        canvasWidth / 3f, canvasHeight / 1.2f,
        canvasWidth / 1.8f, canvasHeight / 1.4f
    )
    cubicTo(
        canvasWidth / 1.8f, canvasHeight / 1.4f,
        canvasWidth / 1.2f, canvasHeight / 2.6f,
        canvasWidth / 2.7f, 10f
    )
    cubicTo(
        canvasWidth / 2.7f, 10f,
        canvasWidth / 2.55f, 50f,
        canvasWidth / 2.7f, canvasHeight / 8f
    )
}

private fun PathMeasure.getAnimatedPath(
    path: Path,
    pathAnimation: Float
): Path {
    setPath(path, false)
    val animatedPath = Path()
    val animatedPathLength = length * pathAnimation
    getSegment(0f, animatedPathLength, animatedPath)
    return animatedPath
}

private fun DrawScope.drawArrowText(
    textMeasurer: TextMeasurer,
    offset: Offset,
    text: String,
    style: TextStyle,
    xTranslation: Float,
    yTranslation: Float
) {
    val textLayoutResult = textMeasurer.measure(text, style)
    rotate(263f, offset) {
        translate(xTranslation, yTranslation) {
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = offset
            )
        }
    }
}

@PreviewAnnotation
@Composable
private fun AddTaskArrowSectionPreview() {
    MyReminderTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            AddTaskArrowSection(
                modifier = Modifier
                    .width(140.dp)
                    .aspectRatio(0.5f)
                    .padding(30.dp)
            )
        }
    }
}