package com.kotlity.feature_reminders.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kotlity.feature_reminders.presentation.RemindersScreen
import kotlinx.serialization.Serializable

@Serializable object RemindersDestination

fun NavGraphBuilder.remindersScreen(
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<RemindersDestination> {
        RemindersScreen(
            onAddClick = onAddClick,
            onEditClick = onEditClick,
            onShowSnackbar = onShowSnackbar
        )
    }
}