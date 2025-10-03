package com.lightfeather.masarify.navigation.routes

import com.lightfeather.domain.model.Account
import com.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRoute(
    val account: Account,
) : Route() {
    override val routeName: String = "com.lightfeather.masarify.navigation.routes.DeleteAccountRoute"
}
