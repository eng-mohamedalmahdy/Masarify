package tech.lightfeather.masarify.navigation.routes

import kotlinx.serialization.Serializable
import tech.lightfeather.masarify.navigation.Route

@Serializable
data object MoreRoute : Route() {
    override val routeName: String = "tech.lightfeather.masarify.navigation.routes.MoreRoute"
}
