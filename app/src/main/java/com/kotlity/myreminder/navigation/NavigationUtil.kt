package com.kotlity.myreminder.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

internal fun <T: Any> NavHostController.navigateTo(
    destination: T,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) = navigate(destination, navOptions)

internal fun NavHostController.goBack() = navigateUp()