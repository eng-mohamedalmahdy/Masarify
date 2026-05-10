package tech.lightfeather.masarify.page.transactions

import androidx.navigation3.runtime.NavKey
import tech.lightfeather.domain.model.transaction.Transaction
import kotlinx.serialization.Serializable

@Serializable
internal data object TransactionsList : NavKey

@Serializable
internal data class ViewTransaction(
    val transaction: Transaction?,
) : NavKey

@Serializable
internal data object AddTransaction : NavKey

@Serializable
internal data class EditTransaction(
    val transaction: Transaction?,
) : NavKey
