package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
class OrderDetailRoute(
    val orderId: String
) : Route() {

    override val route: String = "orderdetailroute"
}