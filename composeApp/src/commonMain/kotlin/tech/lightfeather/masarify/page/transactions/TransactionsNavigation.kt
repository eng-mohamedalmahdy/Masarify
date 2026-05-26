package tech.lightfeather.masarify.page.transactions

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import tech.lightfeather.domain.model.transaction.Transaction

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
