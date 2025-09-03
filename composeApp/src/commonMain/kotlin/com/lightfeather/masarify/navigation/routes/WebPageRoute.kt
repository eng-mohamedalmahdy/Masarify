package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
class WebPageRoute(val url: String) : Route() {

    override val route: String = "webpage"
}