package tech.lightfeather.masarify.navigation.routes

import kotlinx.serialization.Serializable
import tech.lightfeather.masarify.navigation.Route

@Serializable
data object OnBoardingRoute : Route() {
    override val routeName: String = "onboarding"
}
