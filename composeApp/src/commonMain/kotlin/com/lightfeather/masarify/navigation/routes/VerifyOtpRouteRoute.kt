package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable


@Serializable
class VerifyOtpRoute(val countryCode: String, val phoneNumber: String) : Route() {
    override val route = "VerifyOtpRoute"
}
