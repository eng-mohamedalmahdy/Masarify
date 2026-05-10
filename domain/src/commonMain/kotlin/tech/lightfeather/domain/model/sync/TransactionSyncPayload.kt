package tech.lightfeather.domain.model.sync

import kotlinx.serialization.Serializable

@Serializable
data class TransactionSyncPayload(
    val type: String,
    val accountId: Long,
    val name: String,
    val description: String? = null,
    val amount: Double,
    val timestamp: Long,
    val version: Long = 1L,
    val deleted: Boolean = false,
    val remoteId: Long? = null,
    val categoryId: Long? = null,
    val categoryIds: List<Long>? = null,
    val toAccountId: Long? = null,
    val fee: Double? = null,
)
