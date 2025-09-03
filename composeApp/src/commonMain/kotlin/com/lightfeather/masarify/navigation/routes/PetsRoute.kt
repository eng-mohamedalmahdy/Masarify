package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object PetsRoute : Route() {
    override val route = "PetsRoute"
}