package com.kotlity.permissions

import kotlinx.coroutines.flow.Flow

interface PermissionsManager {

    val requiredPermissions: List<Permission>

    val permissionsToAsk: Flow<List<Permission>>

    fun removePermission()

    fun onPermissionResult(permission: Permission, isGranted: Boolean)
}