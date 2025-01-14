package com.kotlity.feature_reminder_editor.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ReminderEditorDestination(
    val id: Long?
)

fun NavGraphBuilder.reminderEditorScreen(
    onBackClick: () -> Unit,
    onUpsertClick: () -> Unit
) {
    composable<ReminderEditorDestination> {

    }
}