package tech.lightfeather.masarify.navigation.routes

import tech.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object RegisterRoute : Route() {
    override val routeName: String = "register"
}
