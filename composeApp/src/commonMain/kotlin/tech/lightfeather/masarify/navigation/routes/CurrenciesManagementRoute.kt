package tech.lightfeather.masarify.navigation.routes

import tech.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object CurrenciesManagementRoute : Route() {
    override val routeName: String = "tech.lightfeather.masarify.navigation.routes.CurrenciesManagementRoute"
}
