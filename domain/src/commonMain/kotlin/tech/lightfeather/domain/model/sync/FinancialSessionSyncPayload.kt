package tech.lightfeather.domain.model.sync

import kotlinx.serialization.Serializable

@Serializable
data class FinancialSessionSyncPayload(
    val name: String? = null,
    val timestamp: Long,
    val accountSnapshots: List<AccountSnapshotSyncPayload> = emptyList(),
    val remoteId: Long? = null,
    val deleted: Boolean = false,
)

@Serializable
data class AccountSnapshotSyncPayload(
    val accountId: Long,
    val startingBalance: Double,
)
