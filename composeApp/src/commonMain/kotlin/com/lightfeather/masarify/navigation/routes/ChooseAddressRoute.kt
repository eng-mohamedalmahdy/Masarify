package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable


@Serializable
class ChooseAddressRoute(
    val source: ChooseAddressNavigationSource = ChooseAddressNavigationSource.CART
) : Route() {
    override val route = "chooseAddress"
}

@Serializable
enum class ChooseAddressNavigationSource {
    CART, ACCOUNT
}