package tech.lightfeather.masarify.page.dashboard

import androidx.navigation3.runtime.NavKey
import tech.lightfeather.domain.model.transaction.Transaction
import kotlinx.serialization.Serializable

@Serializable
internal data object DashboardList : NavKey

@Serializable
internal data class ViewAccountTransactions(
    val accountId: String,
) : NavKey

@Serializable
internal data class ViewTransaction(
    val transaction: Transaction?,
) : NavKey
