package com.lightfeather.masarify.navigation.routes

import com.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object AccountsRoute : Route() {
    override val routeName: String = "com.lightfeather.masarify.navigation.routes.AccountsRoute"
}
