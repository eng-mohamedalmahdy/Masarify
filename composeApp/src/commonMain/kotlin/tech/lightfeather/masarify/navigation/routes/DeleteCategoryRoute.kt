package tech.lightfeather.masarify.navigation.routes

import tech.lightfeather.domain.model.Category
import tech.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class DeleteCategoryRoute(
    val category: Category,
) : Route() {
    override val routeName: String = "tech.lightfeather.masarify.navigation.routes.DeleteCategoryRoute"
}
