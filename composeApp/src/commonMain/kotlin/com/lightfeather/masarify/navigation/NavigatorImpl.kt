package com.lightfeather.masarify.navigation

import androidx.navigation.NavHostController

class NavigatorImpl(
    private val navController: NavHostController,
) : Navigator {
    override fun navigate(route: Route) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    override fun navigateAndClearBackStack(route: Route) {
        navController.navigate(route) {
            popUpTo(0) { inclusive = true }
        }
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun popBackStack() {
        navController.popBackStack()
    }
}
