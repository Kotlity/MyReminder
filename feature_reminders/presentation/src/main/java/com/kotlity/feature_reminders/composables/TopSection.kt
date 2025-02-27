package com.kotlity.feature_reminders.composables

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntOffset
import com.kotlity.core.composables.TopSectionTitle
import com.kotlity.core.ui.theme.MyReminderTheme
import com.kotlity.core.ui.theme.darkBlack
import com.kotlity.core.util.PreviewAnnotation
import com.kotlity.core.resources.R

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    isAddTaskLabelVisible: Boolean,
    isAddTaskClickable: Boolean,
    onAddTaskPositioned: (IntOffset) -> Unit,
    onAddTaskClick: () -> Unit
) {

    Column(modifier = modifier) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen._46dp))
        ) {
            TopSectionTitle(modifier = Modifier.align(Alignment.Center))
            AddTaskSection(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y = dimensionResource(id = R.dimen._3dp))
                    .padding(end = dimensionResource(id = R.dimen._10dp))
                ,
                isAddTaskLabelVisible = isAddTaskLabelVisible,
                isAddTaskClickable = isAddTaskClickable,
                onIconPositioned = {
                    onAddTaskPositioned(it)
                },
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
            isAddTaskClickable = true,
            onAddTaskClick = {
                isAddTaskLabelVisible = !isAddTaskLabelVisible
            },
            onAddTaskPositioned = {}
        )
    }
}