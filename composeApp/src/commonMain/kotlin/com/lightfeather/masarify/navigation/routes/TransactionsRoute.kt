package com.lightfeather.masarify.navigation.routes

import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.masarify.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class TransactionsRoute(
    val openAddDialog: Boolean = false,
    val transactionType: UiTransactionType? = null,
    val fromAccountId: String? = null,
    val categoryId: String? = null,
) : Route() {
    override val routeName: String = "com.lightfeather.masarify.navigation.routes.TransactionsRoute"
}
