package tech.lightfeather.masarify.navigation.routes

import kotlinx.serialization.Serializable
import tech.lightfeather.domain.model.Account
import tech.lightfeather.masarify.navigation.Route

@Serializable
data class DeleteAccountRoute(
    val account: Account,
) : Route() {
    override val routeName: String = "tech.lightfeather.masarify.navigation.routes.DeleteAccountRoute"
}
