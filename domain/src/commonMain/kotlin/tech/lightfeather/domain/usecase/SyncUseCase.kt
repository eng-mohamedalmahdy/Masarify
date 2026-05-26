package tech.lightfeather.domain.usecase

import kotlinx.serialization.json.Json
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.AttachmentEntityType
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.CurrencyType
import tech.lightfeather.domain.model.FinancialSession
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.model.sync.AccountSyncPayload
import tech.lightfeather.domain.model.sync.FinancialSessionSyncPayload
import tech.lightfeather.domain.model.sync.SyncedAccount
import tech.lightfeather.domain.model.sync.SyncedAttachment
import tech.lightfeather.domain.model.sync.SyncedCategory
import tech.lightfeather.domain.model.sync.SyncedCurrency
import tech.lightfeather.domain.model.sync.SyncedFinancialSession
import tech.lightfeather.domain.model.sync.SyncedTransaction
import tech.lightfeather.domain.model.sync.TransactionSyncPayload
import tech.lightfeather.domain.model.transaction.Transaction
import tech.lightfeather.domain.repository.AccountRepository
import tech.lightfeather.domain.repository.AttachmentRepository
import tech.lightfeather.domain.repository.CategoryRepository
import tech.lightfeather.domain.repository.CurrencyRepository
import tech.lightfeather.domain.repository.FinancialSessionRepository
import tech.lightfeather.domain.repository.SyncQueueRepository
import tech.lightfeather.domain.repository.SyncRepository
import tech.lightfeather.domain.repository.TransactionRepository
import tech.lightfeather.domain.repository.UserRepository
import kotlin.time.Clock

class DrainOutboxQueueUseCase(
    private val syncQueueRepository: SyncQueueRepository,
    private val syncRepository: SyncRepository,
) {
    suspend operator fun invoke() {
        val entries = syncQueueRepository.getPendingEntries()
        for (entry in entries) {
            syncRepository.enqueueRemote(entry).foldSuspend(
                onSuccess = { syncQueueRepository.markSent(entry.id, null) },
                onFailure = { error ->
                    if (error is AppError.ConflictError) {
                        syncQueueRepository.markFailed(entry.id)
                    } else {
                        syncQueueRepository.incrementRetry(entry.id)
                        if (entry.retryCount + 1 >= MAX_RETRIES) {
                            syncQueueRepository.markFailed(entry.id)
                        }
                    }
                },
            )
        }
    }

    companion object {
        private const val MAX_RETRIES = 3
    }
}

@Suppress("LongParameterList")
class PullRemoteDeltaUseCase(
    private val syncRepository: SyncRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val currencyRepository: CurrencyRepository,
    private val financialSessionRepository: FinancialSessionRepository,
    private val attachmentRepository: AttachmentRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke() {
        val since = userRepository.getLastSyncAt()
        syncRepository.pullDelta(since).foldSuspend(
            onSuccess = { response ->
                response.currencies.forEach { applyCurrency(it) }
                response.categories.forEach { applyCategory(it) }
                response.accounts.forEach { applyAccount(it) }
                response.transactions.forEach { applyTransaction(it) }
                response.financialSessions.forEach { applySession(it) }
                response.attachments.forEach { applyAttachment(it) }
                userRepository.setLastSyncAt(Clock.System.now().toEpochMilliseconds())
            },
            onFailure = {},
        )
    }

    @Suppress("ReturnCount")
    private suspend fun applyCurrency(synced: SyncedCurrency) {
        if (synced.deleted) {
            val localId = currencyRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull() ?: return
            val currency = currencyRepository.getCurrencyById(localId).getOrNull() ?: return
            currencyRepository.deleteCurrency(currency)
            return
        }
        val localId = currencyRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (localId == null) {
            val existingByKey = synced.resourceKey?.let { currencyRepository.getLocalIdByResourceKey(it).getOrNull() }
            if (existingByKey != null) {
                currencyRepository.updateRemoteId(existingByKey, synced.remoteId)
            } else {
                val newId = currencyRepository.createCurrency(synced.toDomain()).getOrNull() ?: return
                currencyRepository.updateRemoteId(newId, synced.remoteId)
            }
        }
    }

    @Suppress("ReturnCount")
    private suspend fun applyCategory(synced: SyncedCategory) {
        if (synced.deleted) {
            val localId = categoryRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull() ?: return
            val category = categoryRepository.getCategoryById(localId).getOrNull() ?: return
            categoryRepository.deleteCategory(category)
            return
        }
        val localId = categoryRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (localId == null) {
            val existingByKey = synced.resourceKey?.let { categoryRepository.getLocalIdByResourceKey(it).getOrNull() }
            if (existingByKey != null) {
                categoryRepository.updateRemoteId(existingByKey, synced.remoteId)
            } else {
                val newId = categoryRepository.createCategory(synced.toDomain()).getOrNull() ?: return
                categoryRepository.updateRemoteId(newId, synced.remoteId)
            }
        }
    }

    @Suppress("ReturnCount")
    private suspend fun applyAccount(synced: SyncedAccount) {
        if (synced.deleted) {
            val localId = accountRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull() ?: return
            val account = accountRepository.getAccountById(localId).getOrNull() ?: return
            accountRepository.deleteAccount(account)
            return
        }
        val localCurrencyId =
            currencyRepository.getLocalIdByRemoteId(synced.currencyRemoteId).getOrNull() ?: return
        val currency = currencyRepository.getCurrencyById(localCurrencyId).getOrNull() ?: return
        val localId = accountRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (localId == null) {
            val newId = accountRepository.createAccount(synced.toDomain(currency)).getOrNull() ?: return
            accountRepository.updateRemoteId(newId, synced.remoteId)
        }
    }

    @Suppress("ReturnCount")
    private suspend fun applyTransaction(synced: SyncedTransaction) {
        if (synced.deleted) {
            val localId = transactionRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull() ?: return
            transactionRepository.deleteTransaction(localId.toLong())
            return
        }
        val existingId = transactionRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (existingId != null) return
        val account = resolveAccount(synced.accountRemoteId) ?: return
        val receiverAccount = synced.receiverAccountRemoteId?.let { resolveAccount(it) }
        if (synced.receiverAccountRemoteId != null && receiverAccount == null) return
        val transaction = synced.toDomain(account, receiverAccount, categoryRepository) ?: return
        val newId = transactionRepository.createTransaction(transaction).getOrNull() ?: return
        transactionRepository.updateRemoteId(newId, synced.remoteId)
    }

    @Suppress("ReturnCount")
    private suspend fun applySession(synced: SyncedFinancialSession) {
        if (synced.deleted) {
            val localId = financialSessionRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull() ?: return
            financialSessionRepository.deleteSession(localId)
            return
        }
        val existingId = financialSessionRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (existingId != null) return
        val newId =
            financialSessionRepository
                .createSession(
                    FinancialSession(timestamp = synced.timestamp, name = synced.name),
                ).getOrNull() ?: return
        financialSessionRepository.updateRemoteId(newId, synced.remoteId)
    }

    @Suppress("ReturnCount")
    private suspend fun applyAttachment(synced: SyncedAttachment) {
        if (synced.deleted) {
            val existing = attachmentRepository.getByRemoteId(synced.remoteId).getOrNull() ?: return
            attachmentRepository.deleteAttachment(existing.id)
            return
        }
        val existing = attachmentRepository.getByRemoteId(synced.remoteId).getOrNull()
        if (existing != null) return
        val entityType = AttachmentEntityType.fromValue(synced.entityType.lowercase()) ?: return
        val fileContent = syncRepository.downloadAttachment(synced.remoteId).getOrNull() ?: return
        val newId =
            attachmentRepository
                .createAttachment(
                    tech.lightfeather.domain.model.Attachment(
                        id = -1,
                        remoteId = synced.remoteId,
                        entityType = entityType,
                        entityId = null,
                        mimeType = synced.mimeType,
                        fileName = synced.fileName,
                        fileContent = fileContent,
                    ),
                ).getOrNull() ?: return
        attachmentRepository.updateRemoteId(newId, synced.remoteId)
    }

    private suspend fun resolveAccount(remoteId: Long): Account? {
        val localId = accountRepository.getLocalIdByRemoteId(remoteId).getOrNull() ?: return null
        return accountRepository.getAccountById(localId).getOrNull()
    }
}

@Suppress("LongParameterList")
class UploadLocalDataUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val currencyRepository: CurrencyRepository,
    private val financialSessionRepository: FinancialSessionRepository,
    private val syncQueueRepository: SyncQueueRepository,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend operator fun invoke() {
        enqueueUnsynced(
            ids = currencyRepository.getUnsyncedIds().getOrElse(emptyList()),
            entityType = "CURRENCY",
        ) { id ->
            currencyRepository.getCurrencyById(id).getOrNull()?.let { json.encodeToString(Currency.serializer(), it) }
        }

        enqueueUnsynced(
            ids = categoryRepository.getUnsyncedIds().getOrElse(emptyList()),
            entityType = "CATEGORY",
        ) { id ->
            categoryRepository.getCategoryById(id).getOrNull()?.let { json.encodeToString(Category.serializer(), it) }
        }

        enqueueUnsynced(
            ids = accountRepository.getUnsyncedIds().getOrElse(emptyList()),
            entityType = "ACCOUNT",
        ) { id ->
            accountRepository
                .getAccountById(id)
                .getOrNull()
                ?.toSyncPayload(currencyRepository)
                ?.let { json.encodeToString(AccountSyncPayload.serializer(), it) }
        }

        enqueueUnsynced(
            ids = transactionRepository.getUnsyncedIds().getOrElse(emptyList()),
            entityType = "TRANSACTION",
        ) { id ->
            transactionRepository
                .getTransactionById<Transaction>(id)
                .getOrNull()
                ?.toSyncPayload(accountRepository, categoryRepository)
                ?.let { json.encodeToString(TransactionSyncPayload.serializer(), it) }
        }

        enqueueUnsynced(
            ids = financialSessionRepository.getUnsyncedIds().getOrElse(emptyList()),
            entityType = "FINANCIAL_SESSION",
        ) { id ->
            financialSessionRepository
                .getSessionById(id)
                .getOrNull()
                ?.toSyncPayload(accountRepository)
                ?.let { json.encodeToString(FinancialSessionSyncPayload.serializer(), it) }
        }
    }

    private suspend fun enqueueUnsynced(
        ids: List<Int>,
        entityType: String,
        serialize: suspend (Int) -> String?,
    ) {
        for (id in ids) {
            if (syncQueueRepository.hasActiveEntry(id.toLong(), entityType)) continue
            val payload = serialize(id) ?: continue
            syncQueueRepository.insertEntry(
                entityType = entityType,
                operation = "CREATE",
                payload = payload,
                localId = id.toLong(),
                remoteId = null,
            )
        }
    }
}

// ---- Mapping helpers ----

private fun SyncedCurrency.toDomain() =
    Currency(
        name = name,
        sign = sign,
        type = runCatching { CurrencyType.valueOf(type) }.getOrDefault(CurrencyType.TRADITIONAL),
        isDefault = isDefault,
        resourceKey = resourceKey,
        isoCode = isoCode,
    )

private fun SyncedCategory.toDomain() =
    Category(
        id = -1,
        name = name,
        description = description,
        color = color,
        icon = icon,
        isDefault = isDefault,
        resourceKey = resourceKey,
    )

private fun SyncedAccount.toDomain(currency: Currency) =
    Account(
        name = name,
        currency = currency,
        description = description,
        balance = balance,
        color = color,
        logo = logo,
        isDefault = isDefault,
    )

private suspend fun SyncedTransaction.toDomain(
    account: Account,
    receiverAccount: Account?,
    categoryRepository: CategoryRepository,
): Transaction? {
    return when (type.uppercase()) {
        "INCOME" -> {
            val categoryId = categoryRemoteIds.firstOrNull()
            val category =
                if (categoryId != null) {
                    val localId = categoryRepository.getLocalIdByRemoteId(categoryId).getOrNull()
                    if (localId != null) categoryRepository.getCategoryById(localId).getOrNull() else null
                } else {
                    null
                }
            Transaction.Income(
                incomeId = -1,
                incomeName = name ?: "",
                incomeDescription = description,
                incomeAmount = amount,
                incomeTimestamp = timestamp,
                incomeAccount = account,
                source = category ?: Category.Transfer,
            )
        }

        "EXPENSE" -> {
            val categories =
                categoryRemoteIds.mapNotNull { remoteId ->
                    val localId = categoryRepository.getLocalIdByRemoteId(remoteId).getOrNull()
                    if (localId != null) categoryRepository.getCategoryById(localId).getOrNull() else null
                }
            Transaction.Expense(
                expenseId = -1,
                expenseName = name ?: "",
                expenseDescription = description,
                expenseAmount = amount,
                expenseTimestamp = timestamp,
                expenseAccount = account,
                categories = categories,
            )
        }

        "TRANSFER" -> {
            if (receiverAccount == null) return null
            Transaction.Transfer(
                transferId = -1,
                transferName = name ?: "",
                transferDescription = description,
                transferAmount = amount,
                transferTimestamp = timestamp,
                transferAccount = account,
                receiverAccount = receiverAccount,
                fee = fee ?: 0.0,
            )
        }

        else -> null
    }
}
