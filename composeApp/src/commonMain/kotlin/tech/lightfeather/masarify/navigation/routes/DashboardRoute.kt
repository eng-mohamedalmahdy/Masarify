package tech.lightfeather.masarify.navigation.routes

import kotlinx.serialization.Serializable
import tech.lightfeather.masarify.navigation.Route

@Serializable
data object DashboardRoute : Route() {
    override val routeName = "tech.lightfeather.masarify.navigation.routes.DashboardRoute"
}
