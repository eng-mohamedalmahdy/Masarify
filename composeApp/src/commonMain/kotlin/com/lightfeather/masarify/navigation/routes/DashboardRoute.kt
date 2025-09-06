package com.lightfeather.masarify.navigation.routes

import com.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute : Route() {
    override val route = "com.lightfeather.masarify.navigation.routes.DashboardRoute"
}
