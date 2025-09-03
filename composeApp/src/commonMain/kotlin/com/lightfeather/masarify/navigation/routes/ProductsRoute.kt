package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.domain.model.DomainCategory
import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable


@Serializable
class ProductsRoute(val category: DomainCategory) : Route() {
    override val route: String = "products"
}