package com.kotlity.feature_reminders.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.resources.R
import com.kotlity.core.ResourcesConstant._1f
import com.kotlity.core.ResourcesConstant._500

@Composable
fun AddTaskSection(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    isAddTaskLabelVisible: Boolean,
    @StringRes labelRes: Int = R.string.addTask,
    labelStyle: TextStyle = MaterialTheme.typography.labelSmall,
    shape: Shape = RoundedCornerShape(topEnd = dimensionResource(id = R.dimen._30dp), bottomEnd = dimensionResource(id = R.dimen._30dp)),
    border: BorderStroke = BorderStroke(width = dimensionResource(id = R.dimen._1dp), color = MaterialTheme.colorScheme.secondary),
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    @DrawableRes addIconRes: Int = R.drawable.add_main_task,
    addIconDescription: String? = stringResource(id = R.string.addTaskIconDescription),
    onIconPositioned: (IntOffset) -> Unit,
    onAddTaskClick: () -> Unit
) {

    val addIconOffsetInPx by animateIntAsState(targetValue = if (isAddTaskLabelVisible) 0 else with(LocalDensity.current) {
        dimensionResource(id = R.dimen.minus25dp).roundToPx()
    },
        label = "",
        animationSpec = tween(_500)
    )

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Image(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen._36dp))
                .zIndex(_1f)
                .offset {
                    IntOffset(addIconOffsetInPx, 0)
                }
                .onGloballyPositioned { layoutCoordinates ->
                    val position = layoutCoordinates.positionInRoot()
                    val size = layoutCoordinates.size
                    val xPosition = position.x.toInt() - size.width / 2f
                    val yPosition = position.y.toInt() + size.height * 1.2f
                    onIconPositioned(IntOffset(x = xPosition.toInt(), y = yPosition.toInt()))
                }
                .clickable(onClick = onAddTaskClick),
            painter = painterResource(id = addIconRes),
            contentDescription = addIconDescription
        )
        AnimatedContent(
            targetState = isAddTaskLabelVisible,
            transitionSpec = {
                if (targetState) { // card appearance
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(_500)) // specifies how the target content should appear
                        .togetherWith(ExitTransition.None) // specifies how the current content should disappear
                } else { // card disappearance
                    EnterTransition.None
                        .togetherWith(slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(_500)))
                }

            },
            label = stringResource(id = R.string.addTaskAnimatedContentLabel)
        ) { isTaskLabelVisible ->
            if (isTaskLabelVisible) {
                Card(
                    modifier = Modifier.offset(x = dimensionResource(id = R.dimen.minus8dp)),
                    onClick = onAddTaskClick,
                    shape = shape,
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    border = border
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen._15dp),
                            vertical = dimensionResource(id = R.dimen._3dp)
                        ),
                        text = stringResource(id = labelRes),
                        style = labelStyle,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@PreviewAnnotation
@Composable
private fun AddTaskSectionPreview() {
    var isShowAddTaskLabel by rememberSaveable {
        mutableStateOf(true)
    }
    MyReminderTheme {
        Box(
            modifier = Modifier
                .padding(top = 20.dp, end = 20.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            AddTaskSection(
                isAddTaskLabelVisible = isShowAddTaskLabel,
                onIconPositioned = {},
                onAddTaskClick = { isShowAddTaskLabel = !isShowAddTaskLabel }
            )

            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = { isShowAddTaskLabel = !isShowAddTaskLabel },
            ) {
                Text(text = (if (isShowAddTaskLabel) "hide" else "show") + "label")
            }
        }
    }
}