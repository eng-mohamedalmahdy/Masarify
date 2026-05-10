package tech.lightfeather.domain.model.sync

import kotlinx.serialization.Serializable

@Serializable
data class AccountSyncPayload(
    val name: String,
    val description: String? = null,
    val balance: Double,
    val currencyId: Long,
    val color: String,
    val logo: String? = null,
    val isDefault: Boolean = false,
    val remoteId: Long? = null,
    val version: Long = 1L,
    val deleted: Boolean = false,
)
