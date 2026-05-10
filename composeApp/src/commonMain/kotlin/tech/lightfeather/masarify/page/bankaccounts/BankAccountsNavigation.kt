package tech.lightfeather.masarify.page.bankaccounts

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object BankAccountsList : NavKey

@Serializable
internal data object AddBankAccount : NavKey

@Serializable
internal data class ViewBankAccount(
    val accountId: String,
) : NavKey

@Serializable
internal data class UpdateBankAccount(
    val accountId: String,
) : NavKey
