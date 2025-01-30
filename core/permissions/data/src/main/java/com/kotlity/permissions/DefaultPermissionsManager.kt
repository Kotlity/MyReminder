package com.kotlity.permissions

import android.os.Build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultPermissionsManager: PermissionsManager {

    private fun retrieveRequiredPermissions(): List<Permission> {
        val permissions = mutableListOf<Permission>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permissions.add(Permission.NOTIFICATIONS)
        return permissions
    }

    override val requiredPermissions: List<Permission> = retrieveRequiredPermissions()

    private val _permissionsToAsk: MutableStateFlow<List<Permission>> = MutableStateFlow(emptyList())

    override val permissionsToAsk: Flow<List<Permission>> = _permissionsToAsk.asStateFlow()

    override fun removePermission() {
        _permissionsToAsk.update { permissions ->
            if (permissions.isNotEmpty()) permissions.drop(1) else permissions
        }
    }

    override fun onPermissionResult(permission: Permission, isGranted: Boolean) {
        if (!isGranted && !_permissionsToAsk.value.contains(permission)) _permissionsToAsk.update { permissions ->
            permissions + permission
        }
    }
}