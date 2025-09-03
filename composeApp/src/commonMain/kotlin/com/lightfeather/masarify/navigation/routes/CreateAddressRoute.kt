package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.domain.model.DomainAddress
import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class CreateAddressRoute(val address: DomainAddress?) : Route() {
    override val route: String = "createAddressPage"
}