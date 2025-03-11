package com.kotlity.myreminder

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kotlity.core.util.DefaultSnackbarHost
import com.kotlity.core.util.ObserveAsEvents
import com.kotlity.feature_reminders.navigation.RemindersDestination
import com.kotlity.myreminder.navigation.MyReminderNavHost
import com.kotlity.myreminder.navigation.navigateTo
import kotlinx.coroutines.flow.Flow

@Composable
internal fun MyReminderApp(
    modifier: Modifier = Modifier,
    shouldNavigateToRemindersScreen: Flow<Boolean>
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val navHostController = rememberNavController()

    ObserveAsEvents(shouldNavigateToRemindersScreen) {
        val isCurrentRemindersScreen = navHostController.currentBackStackEntry?.destination?.hasRoute(RemindersDestination::class)
        if (isCurrentRemindersScreen == false) navHostController.navigateTo(RemindersDestination)
    }

    MyReminderApp(
        modifier = modifier,
        navHostController = navHostController,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun MyReminderApp(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState
) {

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            DefaultSnackbarHost(snackbarHostState = snackbarHostState)
        }
    ) { paddingValues ->
        MyReminderNavHost(
            modifier = Modifier.padding(paddingValues),
            navHostController = navHostController,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short
                ) == SnackbarResult.ActionPerformed
            }
        )
    }
}