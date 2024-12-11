package com.kotlity.feature_reminders.presentation.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import com.kotlity.core.presentation.ui.theme.MyReminderTheme
import com.kotlity.core.presentation.ui.theme.darkBlack
import com.kotlity.core.presentation.util.PreviewAnnotation
import com.kotlity.core.resources.R

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int = R.string.app_name,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge,
    isAddTaskLabelVisible: Boolean,
    onAddTaskPositioned: (IntOffset) -> Unit,
    onAddTaskClick: () -> Unit
) {

    Column(modifier = modifier) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen._46dp))
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = titleRes),
                style = titleStyle
            )
            AddTaskSection(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y = dimensionResource(id = R.dimen._3dp))
                    .padding(end = dimensionResource(id = R.dimen._10dp))
                    .onGloballyPositioned { layoutCoordinates ->
                        val position = layoutCoordinates.positionInRoot()
                        val size = layoutCoordinates.size
                        val x = position.x.toInt()
                        val y = position.y.toInt() + size.height
                        onAddTaskPositioned(IntOffset(x, y))
                    }
                ,
                isAddTaskLabelVisible = isAddTaskLabelVisible,
                onAddTaskClick = onAddTaskClick
            )
        }
        Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen._25dp)))
        HorizontalDivider(color = darkBlack)
    }
}

@PreviewAnnotation
@Composable
private fun TopSectionPreview() {
    MyReminderTheme {
        var isAddTaskLabelVisible by rememberSaveable {
            mutableStateOf(true)
        }

        TopSection(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .systemBarsPadding(),
            isAddTaskLabelVisible = isAddTaskLabelVisible,
            onAddTaskClick = {
                isAddTaskLabelVisible = !isAddTaskLabelVisible
            },
            onAddTaskPositioned = {}
        )
    }
}