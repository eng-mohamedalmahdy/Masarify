package com.lightfeather.happytail.navigation


interface Navigator {
    fun navigate(route: Route)
    fun navigateAndClearBackStack(route: Route)
    fun navigateUp()
    fun popBackStack()
}