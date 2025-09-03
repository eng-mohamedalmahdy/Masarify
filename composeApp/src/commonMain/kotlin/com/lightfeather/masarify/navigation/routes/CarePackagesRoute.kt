package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class CarePackagesRoute(override val route: String = "care_packages") : Route()
