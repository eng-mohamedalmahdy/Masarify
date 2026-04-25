package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.CurrencyType
import com.lightfeather.domain.model.FinancialSession
import com.lightfeather.domain.model.sync.SyncedAccount
import com.lightfeather.domain.model.sync.SyncedCategory
import com.lightfeather.domain.model.sync.SyncedCurrency
import com.lightfeather.domain.model.sync.SyncedFinancialSession
import com.lightfeather.domain.model.sync.SyncedTransaction
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.repository.CategoryRepository
import com.lightfeather.domain.repository.CurrencyRepository
import com.lightfeather.domain.repository.FinancialSessionRepository
import com.lightfeather.domain.repository.SyncQueueRepository
import com.lightfeather.domain.repository.SyncRepository
import com.lightfeather.domain.repository.TransactionRepository
import com.lightfeather.domain.repository.UserRepository
import kotlinx.serialization.json.Json
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
                onFailure = {
                    syncQueueRepository.incrementRetry(entry.id)
                    if (entry.retryCount + 1 >= MAX_RETRIES) {
                        syncQueueRepository.markFailed(entry.id)
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
                userRepository.setLastSyncAt(Clock.System.now().toEpochMilliseconds())
            },
            onFailure = {},
        )
    }

    private suspend fun applyCurrency(synced: SyncedCurrency) {
        if (synced.deleted) return
        val localId = currencyRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (localId == null) {
            val newId = currencyRepository.createCurrency(synced.toDomain()).getOrNull() ?: return
            currencyRepository.updateRemoteId(newId, synced.remoteId)
        }
    }

    private suspend fun applyCategory(synced: SyncedCategory) {
        if (synced.deleted) return
        val localId = categoryRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (localId == null) {
            val newId = categoryRepository.createCategory(synced.toDomain()).getOrNull() ?: return
            categoryRepository.updateRemoteId(newId, synced.remoteId)
        }
    }

    @Suppress("ReturnCount")
    private suspend fun applyAccount(synced: SyncedAccount) {
        if (synced.deleted) return
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
        if (synced.deleted) return
        val existingId = transactionRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (existingId != null) return
        val account = resolveAccount(synced.accountRemoteId) ?: return
        val transaction = synced.toDomain(account, categoryRepository) ?: return
        val newId = transactionRepository.createTransaction(transaction).getOrNull() ?: return
        transactionRepository.updateRemoteId(newId, synced.remoteId)
    }

    @Suppress("ReturnCount")
    private suspend fun applySession(synced: SyncedFinancialSession) {
        if (synced.deleted) return
        val existingId = financialSessionRepository.getLocalIdByRemoteId(synced.remoteId).getOrNull()
        if (existingId != null) return
        val newId =
            financialSessionRepository.createSession(
                FinancialSession(timestamp = synced.timestamp, name = synced.name),
            ).getOrNull() ?: return
        financialSessionRepository.updateRemoteId(newId, synced.remoteId)
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
            accountRepository.getAccountById(id).getOrNull()?.let { json.encodeToString(Account.serializer(), it) }
        }

        enqueueUnsynced(
            ids = transactionRepository.getUnsyncedIds().getOrElse(emptyList()),
            entityType = "TRANSACTION",
        ) { id ->
            transactionRepository.getTransactionById<Transaction>(id).getOrNull()
                ?.let { json.encodeToString(Transaction.serializer(), it) }
        }

        enqueueUnsynced(
            ids = financialSessionRepository.getUnsyncedIds().getOrElse(emptyList()),
            entityType = "FINANCIAL_SESSION",
        ) { id ->
            financialSessionRepository.getSessionById(id).getOrNull()
                ?.let { json.encodeToString(FinancialSession.serializer(), it) }
        }
    }

    private suspend fun enqueueUnsynced(
        ids: List<Int>,
        entityType: String,
        serialize: suspend (Int) -> String?,
    ) {
        for (id in ids) {
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
                id = -1,
                name = name ?: "",
                description = description,
                amount = amount,
                timestamp = timestamp,
                account = account,
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
                id = -1,
                name = name ?: "",
                description = description,
                amount = amount,
                timestamp = timestamp,
                account = account,
                categories = categories,
            )
        }

        else -> null
    }
}
