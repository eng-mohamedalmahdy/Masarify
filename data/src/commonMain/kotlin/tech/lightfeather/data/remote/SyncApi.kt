package tech.lightfeather.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.model.sync.SyncPullResponse
import tech.lightfeather.domain.model.sync.SyncQueueEntry
import tech.lightfeather.domain.model.sync.SyncedAccount
import tech.lightfeather.domain.model.sync.SyncedAccountSnapshot
import tech.lightfeather.domain.model.sync.SyncedAttachment
import tech.lightfeather.domain.model.sync.SyncedCategory
import tech.lightfeather.domain.model.sync.SyncedCurrency
import tech.lightfeather.domain.model.sync.SyncedFinancialSession
import tech.lightfeather.domain.model.sync.SyncedTransaction

// Network errors require generic exception handling for graceful degradation
@Suppress("TooGenericExceptionCaught")
class SyncApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun enqueue(entry: SyncQueueEntry): DomainResult<Unit> =
        try {
            val response =
                httpClient
                    .post("$baseUrl/sync/enqueue") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            EnqueueRequest(
                                entityType = entry.entityType,
                                operation = entry.operation,
                                payload = entry.payload,
                                localEntityId = entry.localId,
                                remoteEntityId = entry.remoteId,
                            ),
                        )
                    }.body<SyncApiResponse<EnqueueResponseDto>>()
            if (response.success) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Failure(AppError.InternalError(response.message ?: "Enqueue failed"))
            }
        } catch (e: ClientRequestException) {
            Napier.e("Sync enqueue failed", e, tag = "SyncApi")
            when (e.response.status) {
                HttpStatusCode.Conflict ->
                    DomainResult.Failure(AppError.ConflictError("Version conflict: server has newer data"))
                HttpStatusCode(402, "Payment Required") ->
                    DomainResult.Failure(AppError.UpgradeRequired("Pro subscription required"))
                else -> DomainResult.Failure(AppError.InternalError(e.message))
            }
        } catch (e: Exception) {
            Napier.e("Sync enqueue failed", e, tag = "SyncApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Enqueue failed"))
        }

    suspend fun pullDelta(since: Long): DomainResult<SyncPullResponse> =
        try {
            val response =
                httpClient
                    .get("$baseUrl/sync/pull?since=$since")
                    .body<SyncApiResponse<SyncPullResponseDto>>()
            if (response.success && response.data != null) {
                DomainResult.Success(response.data.toDomain())
            } else {
                DomainResult.Failure(AppError.InternalError(response.message ?: "Pull delta failed"))
            }
        } catch (e: ClientRequestException) {
            Napier.e("Sync pull failed", e, tag = "SyncApi")
            if (e.response.status == HttpStatusCode(402, "Payment Required")) {
                DomainResult.Failure(AppError.UpgradeRequired("Pro subscription required"))
            } else {
                DomainResult.Failure(AppError.InternalError(e.message))
            }
        } catch (e: Exception) {
            Napier.e("Sync pull failed", e, tag = "SyncApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Pull delta failed"))
        }

    suspend fun downloadAttachment(remoteId: Long): DomainResult<ByteArray> =
        try {
            val bytes = httpClient.get("$baseUrl/attachments/$remoteId/download").body<ByteArray>()
            DomainResult.Success(bytes)
        } catch (e: ClientRequestException) {
            Napier.e("Attachment download failed", e, tag = "SyncApi")
            if (e.response.status == HttpStatusCode(402, "Payment Required")) {
                DomainResult.Failure(AppError.UpgradeRequired("Pro subscription required"))
            } else {
                DomainResult.Failure(AppError.InternalError(e.message))
            }
        } catch (e: Exception) {
            Napier.e("Attachment download failed", e, tag = "SyncApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Download failed"))
        }
}

private fun SyncPullResponseDto.toDomain() =
    SyncPullResponse(
        accounts = accounts.map { it.toDomain() },
        transactions = transactions.map { it.toDomain() },
        categories = categories.map { it.toDomain() },
        currencies = currencies.map { it.toDomain() },
        financialSessions = financialSessions.map { it.toDomain() },
        attachments = attachments.map { it.toDomain() },
    )

private fun AttachmentSyncResponseDto.toDomain() =
    SyncedAttachment(
        remoteId = id,
        entityType = entityType,
        entityRemoteId = entityRemoteId,
        mimeType = mimeType,
        fileName = fileName,
        sizeBytes = sizeBytes,
        createdAt = createdAt,
        deleted = deleted,
    )

private fun AccountResponseDto.toDomain() =
    SyncedAccount(
        remoteId = id,
        name = name,
        description = description,
        balance = balance,
        currencyRemoteId = currency.id,
        color = color,
        logo = logo,
        isDefault = isDefault,
        deleted = deleted,
    )

private fun TransactionResponseDto.toDomain() =
    SyncedTransaction(
        remoteId = id,
        type = type,
        accountRemoteId = accountId,
        name = name,
        description = description,
        amount = amount,
        timestamp = timestamp,
        categoryRemoteIds = categoryIds ?: emptyList(),
        receiverAccountRemoteId = toAccountId,
        fee = fee,
        deleted = deleted,
    )

private fun CategoryResponseDto.toDomain() =
    SyncedCategory(
        remoteId = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        isDefault = isDefault,
        resourceKey = resourceKey,
        deleted = deleted,
    )

private fun CurrencyResponseDto.toDomain() =
    SyncedCurrency(
        remoteId = id,
        name = name,
        sign = sign,
        type = type,
        isDefault = isDefault,
        resourceKey = resourceKey,
        isoCode = isoCode,
        deleted = deleted,
    )

private fun FinancialSessionResponseDto.toDomain() =
    SyncedFinancialSession(
        remoteId = id,
        name = name,
        timestamp = timestamp,
        accountSnapshots =
            accountSnapshots.map {
                SyncedAccountSnapshot(
                    accountRemoteId = it.accountId,
                    startingBalance = it.startingBalance,
                )
            },
        deleted = deleted,
    )

// ---- DTOs ----

@Serializable
private data class SyncApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
)

@Serializable
private data class EnqueueRequest(
    val entityType: String,
    val operation: String,
    val payload: String,
    val localEntityId: Long? = null,
    val remoteEntityId: Long? = null,
)

@Serializable
private data class EnqueueResponseDto(
    val actionId: String,
)

@Serializable
private data class SyncPullResponseDto(
    val accounts: List<AccountResponseDto> = emptyList(),
    val transactions: List<TransactionResponseDto> = emptyList(),
    val categories: List<CategoryResponseDto> = emptyList(),
    val currencies: List<CurrencyResponseDto> = emptyList(),
    val financialSessions: List<FinancialSessionResponseDto> = emptyList(),
    val attachments: List<AttachmentSyncResponseDto> = emptyList(),
)

@Serializable
private data class AttachmentSyncResponseDto(
    val id: Long,
    val entityType: String,
    val entityRemoteId: Long,
    val mimeType: String,
    val fileName: String,
    val sizeBytes: Long,
    val createdAt: Long,
    val deleted: Boolean,
)

@Serializable
private data class CurrencyEmbeddedDto(
    val id: Long,
    val name: String,
    val sign: String,
    val type: String,
)

@Serializable
private data class AccountResponseDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val balance: Double,
    val currency: CurrencyEmbeddedDto,
    val color: String,
    val logo: String? = null,
    val isDefault: Boolean,
    val deleted: Boolean,
)

@Serializable
private data class TransactionResponseDto(
    val id: Long,
    val type: String,
    val accountId: Long,
    val name: String? = null,
    val description: String? = null,
    val amount: Double,
    val timestamp: Long,
    val categoryIds: List<Long>? = null,
    val toAccountId: Long? = null,
    val fee: Double? = null,
    val deleted: Boolean,
)

@Serializable
private data class CategoryResponseDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val color: String,
    val icon: String,
    val isDefault: Boolean,
    val resourceKey: String? = null,
    val deleted: Boolean,
)

@Serializable
private data class CurrencyResponseDto(
    val id: Long,
    val name: String,
    val sign: String,
    val type: String,
    val isDefault: Boolean,
    val resourceKey: String? = null,
    val isoCode: String? = null,
    val deleted: Boolean,
)

@Serializable
private data class AccountSnapshotDto(
    val accountId: Long,
    val startingBalance: Double,
)

@Serializable
private data class FinancialSessionResponseDto(
    val id: Long,
    val name: String? = null,
    val timestamp: Long,
    val accountSnapshots: List<AccountSnapshotDto> = emptyList(),
    val deleted: Boolean,
)
