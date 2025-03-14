package com.kotlity.feature_reminder_editor.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kotlity.feature_reminder_editor.ReminderEditorScreen
import kotlinx.serialization.Serializable

@Serializable
data class ReminderEditorDestination(
    val id: Long?
)

fun NavGraphBuilder.reminderEditorScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<ReminderEditorDestination> {
        ReminderEditorScreen(
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar
        )
    }
}