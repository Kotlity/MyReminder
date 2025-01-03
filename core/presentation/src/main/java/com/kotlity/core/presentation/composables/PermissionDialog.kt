package com.kotlity.core.presentation.composables

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kotlity.core.resources.R.string

@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    @StringRes okText: Int = string.ok,
    @StringRes dismissText: Int = string.dismiss,
    @StringRes grantPermissionText: Int = string.grantPermission,
    @StringRes titleText: Int = string.title,
    onDismissClick: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissClick,
        confirmButton = {
            TextButton(onClick = if (isPermanentlyDeclined) onGoToAppSettingsClick else onOkClick) {
                Text(text = stringResource(id = if (isPermanentlyDeclined) grantPermissionText else okText))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClick,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onErrorContainer)
            ) {
                Text(text = stringResource(id = dismissText))
            }
        },
        title = {
            Text(text = stringResource(id = titleText))
        },
        text = {
            Text(text = stringResource(id = permissionTextProvider.getDescriptionStringResource(isPermanentlyDeclined)))
        }
    )
}

interface PermissionTextProvider {
    @StringRes fun getDescriptionStringResource(isPermissionPermanentlyDeclined: Boolean): Int
}

class NotificationsPermissionTextProvider: PermissionTextProvider {

    override fun getDescriptionStringResource(isPermissionPermanentlyDeclined: Boolean): Int {
        return if (isPermissionPermanentlyDeclined) string.notificationsPermissionPermanentlyDeclinedText
            else string.notificationsPermissionText
    }
}