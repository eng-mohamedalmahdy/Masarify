package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class PetDetailRoute(val petId: Int? = null) : Route() {
    override val route = "PetDetailRoute"
}