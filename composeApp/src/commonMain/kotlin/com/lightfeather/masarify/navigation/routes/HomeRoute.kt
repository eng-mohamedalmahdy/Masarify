package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute : Route() {
    override val route = "HomeRoute"
}

@Serializable
object ProfileRoute : Route() {
    override val route = "ProfileRoute"
}
