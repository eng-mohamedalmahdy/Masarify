package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable


@Serializable
data object PaymentMethodsRoute : Route() {
    override val route: String = "payment_methods"
}