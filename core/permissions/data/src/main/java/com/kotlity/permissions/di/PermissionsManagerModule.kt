package com.kotlity.permissions.di

import com.kotlity.permissions.DefaultPermissionsManager
import com.kotlity.permissions.PermissionsManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val permissionsManagerModule = module {
    singleOf(::DefaultPermissionsManager) { bind<PermissionsManager>() }
}