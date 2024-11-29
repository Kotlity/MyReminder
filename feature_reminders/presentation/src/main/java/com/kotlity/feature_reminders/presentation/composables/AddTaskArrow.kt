package com.kotlity.feature_reminders.presentation.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kotlity.core.resources.R.drawable.add_main_task_arrow
import com.kotlity.core.resources.R.string.addYourTask

@Composable
fun AddTaskArrow(
    modifier: Modifier = Modifier,
    isAddTaskArrowVisible: Boolean,
    @DrawableRes addArrowRes: Int = add_main_task_arrow,
    @StringRes labelRes: Int = addYourTask
) {


}