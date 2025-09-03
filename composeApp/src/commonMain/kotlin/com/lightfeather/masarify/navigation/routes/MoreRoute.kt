package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
object MoreRoute : Route() {

    override val route: String = "moreroute"
}