package tech.lightfeather.masarify.navigation.routes

import kotlinx.serialization.Serializable
import tech.lightfeather.masarify.navigation.Route

@Serializable
data class VerifyEmailRoute(
    val token: String,
) : Route() {
    override val routeName: String = "verify-email"
}
