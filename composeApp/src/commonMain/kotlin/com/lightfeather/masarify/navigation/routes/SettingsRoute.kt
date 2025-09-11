package com.lightfeather.masarify.navigation.routes

import com.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute : Route() {

    override val route: String = "com.lightfeather.masarify.navigation.routes.SettingsRoute"
}
