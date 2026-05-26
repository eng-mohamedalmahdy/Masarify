package tech.lightfeather.masarify.navigation.routes

import kotlinx.serialization.Serializable
import tech.lightfeather.masarify.navigation.Route

@Serializable
data object LoginRoute : Route() {
    override val routeName: String = "login"
}
