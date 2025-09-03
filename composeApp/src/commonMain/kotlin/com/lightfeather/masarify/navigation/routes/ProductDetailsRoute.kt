package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class ProductDetailsRoute(val productId: String) : Route() {
    override val route: String = "productDetails"
}
