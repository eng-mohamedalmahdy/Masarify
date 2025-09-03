package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
class CreateExpenseRoute : Route() {

    override val route: String = "createexpenseroute"
}