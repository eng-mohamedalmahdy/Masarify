package tech.lightfeather.domain.model.sync

data class SyncPullResponse(
    val accounts: List<SyncedAccount> = emptyList(),
    val transactions: List<SyncedTransaction> = emptyList(),
    val categories: List<SyncedCategory> = emptyList(),
    val currencies: List<SyncedCurrency> = emptyList(),
    val financialSessions: List<SyncedFinancialSession> = emptyList(),
    val attachments: List<SyncedAttachment> = emptyList(),
)

data class SyncedAccount(
    val remoteId: Long,
    val name: String,
    val description: String?,
    val balance: Double,
    val currencyRemoteId: Long,
    val color: String,
    val logo: String?,
    val isDefault: Boolean,
    val deleted: Boolean,
)

data class SyncedTransaction(
    val remoteId: Long,
    val type: String,
    val accountRemoteId: Long,
    val name: String?,
    val description: String?,
    val amount: Double,
    val timestamp: Long,
    val categoryRemoteIds: List<Long>,
    val receiverAccountRemoteId: Long?,
    val fee: Double?,
    val deleted: Boolean,
)

data class SyncedCategory(
    val remoteId: Long,
    val name: String,
    val description: String?,
    val color: String,
    val icon: String,
    val isDefault: Boolean,
    val resourceKey: String?,
    val deleted: Boolean,
)

data class SyncedCurrency(
    val remoteId: Long,
    val name: String,
    val sign: String,
    val type: String,
    val isDefault: Boolean,
    val resourceKey: String?,
    val isoCode: String?,
    val deleted: Boolean,
)

data class SyncedFinancialSession(
    val remoteId: Long,
    val name: String?,
    val timestamp: Long,
    val accountSnapshots: List<SyncedAccountSnapshot>,
    val deleted: Boolean,
)

data class SyncedAccountSnapshot(
    val accountRemoteId: Long,
    val startingBalance: Double,
)

data class SyncedAttachment(
    val remoteId: Long,
    val entityType: String,
    val entityRemoteId: Long,
    val mimeType: String,
    val fileName: String,
    val sizeBytes: Long,
    val createdAt: Long,
    val deleted: Boolean,
)
