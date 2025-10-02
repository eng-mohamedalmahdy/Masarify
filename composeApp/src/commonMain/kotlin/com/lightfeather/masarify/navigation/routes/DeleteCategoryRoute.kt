package com.lightfeather.masarify.navigation.routes

import com.lightfeather.domain.model.Category
import com.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class DeleteCategoryRoute(
    val category: Category,
) : Route() {
    override val route: String = "com.lightfeather.masarify.navigation.routes.DeleteCategoryRoute"
}
