package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
class WalletRoute : Route() {

    override val route: String = "wallet"
}