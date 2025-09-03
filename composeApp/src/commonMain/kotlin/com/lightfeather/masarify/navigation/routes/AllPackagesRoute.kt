package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class AllPackagesRoute(override val route: String = "all_packages") : Route()
