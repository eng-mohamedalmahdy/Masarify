package com.lightfeather.masarify.navigation.routes

import com.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object OnBoardingRoute : Route() {
    override val routeName: String = "onboarding"
}
