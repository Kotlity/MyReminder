package com.kotlity.myreminder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kotlity.feature_reminder_editor.navigation.ReminderEditorDestination
import com.kotlity.feature_reminder_editor.navigation.reminderEditorScreen
import com.kotlity.feature_reminders.navigation.RemindersDestination
import com.kotlity.feature_reminders.navigation.remindersScreen

@Composable
internal fun MyReminderNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {

    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = RemindersDestination
    ) {
        remindersScreen(
            onAddClick = { navHostController.navigateTo(ReminderEditorDestination(id = null)) },
            onEditClick = { id -> navHostController.navigateTo(ReminderEditorDestination(id = id)) },
            onShowSnackbar = onShowSnackbar
        )
        reminderEditorScreen(
            onBackClick = { navHostController.goBack() },
            onShowSnackbar = onShowSnackbar
        )
    }
}