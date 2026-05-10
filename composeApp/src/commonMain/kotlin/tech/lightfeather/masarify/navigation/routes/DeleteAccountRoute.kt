package tech.lightfeather.masarify.navigation.routes

import tech.lightfeather.domain.model.Account
import tech.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRoute(
    val account: Account,
) : Route() {
    override val routeName: String = "tech.lightfeather.masarify.navigation.routes.DeleteAccountRoute"
}
