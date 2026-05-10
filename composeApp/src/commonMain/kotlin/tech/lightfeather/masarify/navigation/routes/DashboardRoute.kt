package tech.lightfeather.masarify.navigation.routes

import tech.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute : Route() {
    override val routeName = "tech.lightfeather.masarify.navigation.routes.DashboardRoute"
}
