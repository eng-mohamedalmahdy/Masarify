package tech.lightfeather.masarify.navigation.routes

import kotlinx.serialization.Serializable
import tech.lightfeather.domain.model.Category
import tech.lightfeather.masarify.navigation.Route

@Serializable
data class DeleteCategoryRoute(
    val category: Category,
) : Route() {
    override val routeName: String = "tech.lightfeather.masarify.navigation.routes.DeleteCategoryRoute"
}
