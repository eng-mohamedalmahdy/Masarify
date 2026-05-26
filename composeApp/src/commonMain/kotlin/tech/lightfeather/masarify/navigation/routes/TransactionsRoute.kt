package tech.lightfeather.masarify.navigation.routes

import kotlinx.serialization.Serializable
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.masarify.navigation.Route

@Serializable
data class TransactionsRoute(
    val openAddDialog: Boolean = false,
    val transactionType: UiTransactionType? = null,
    val fromAccountId: String? = null,
    val categoryId: String? = null,
) : Route() {
    override val routeName: String = "tech.lightfeather.masarify.navigation.routes.TransactionsRoute"
}
