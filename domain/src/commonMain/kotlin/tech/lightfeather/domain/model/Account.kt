package tech.lightfeather.domain.model

import kotlinx.serialization.Serializable

typealias Accounts = List<Account>

@Serializable
data class Account(
    val name: String,
    val currency: Currency,
    val description: String?,
    val balance: Double,
    val color: String,
    val logo: String?,
    val id: Int = -1,
    val isDefault: Boolean = false,
)
