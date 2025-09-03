package com.lightfeather.masarify.navigation

import kotlinx.serialization.Serializable

@Serializable
abstract class Route {
    abstract val route: String
}