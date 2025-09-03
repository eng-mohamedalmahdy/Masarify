package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable


@Serializable
object OrdersRoute : Route() {
    override val route: String = "orders"
}