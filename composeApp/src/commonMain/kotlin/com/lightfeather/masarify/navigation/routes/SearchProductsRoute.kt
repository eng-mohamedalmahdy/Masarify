package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable


@Serializable
data class SearchProductsRoute(override val route: String = "searchProducts") : Route()