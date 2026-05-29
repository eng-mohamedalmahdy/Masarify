@file:Suppress("EmptyFunctionBlock", "TooManyFunctions")

package tech.lightfeather.domain.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.Attachment
import tech.lightfeather.domain.model.AttachmentEntityType
import tech.lightfeather.domain.model.BankName
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.CurrencyExchangeRate
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.FinancialSession
import tech.lightfeather.domain.model.PagedData
import tech.lightfeather.domain.model.SubscriptionPlan
import tech.lightfeather.domain.model.SubscriptionStatus
import tech.lightfeather.domain.model.SubscriptionStatusType
import tech.lightfeather.domain.model.UserData
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.model.sync.SyncPullResponse
import tech.lightfeather.domain.model.sync.SyncQueueEntry
import tech.lightfeather.domain.model.transaction.Transaction
import tech.lightfeather.domain.model.transaction.TransactionFilter
import tech.lightfeather.domain.repository.AccountRepository
import tech.lightfeather.domain.repository.AttachmentRepository
import tech.lightfeather.domain.repository.AuthRepository
import tech.lightfeather.domain.repository.BackupRepository
import tech.lightfeather.domain.repository.BankNameRepository
import tech.lightfeather.domain.repository.CategoryRepository
import tech.lightfeather.domain.repository.CurrencyExchangeRateRepository
import tech.lightfeather.domain.repository.CurrencyRepository
import tech.lightfeather.domain.repository.FinancialSessionRepository
import tech.lightfeather.domain.repository.RevenueCatSdk
import tech.lightfeather.domain.repository.SubscriptionRepository
import tech.lightfeather.domain.repository.SyncQueueRepository
import tech.lightfeather.domain.repository.SyncRepository
import tech.lightfeather.domain.repository.TransactionRepository
import tech.lightfeather.domain.repository.UserRepository
import kotlin.reflect.KClass

// ---------------------------------------------------------------------------
// Account
// ---------------------------------------------------------------------------

class FakeAccountRepository(
    var shouldFail: Boolean = false,
) : AccountRepository {
    private var seq = 1
    val store = mutableMapOf<Int, Account>()

    override suspend fun createAccount(account: Account): DomainResult<Int> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        val id = seq++
        store[id] = account.copy(id = id)
        return DomainResult.Success(id)
    }

    override suspend fun updateAccount(account: Account): DomainResult<Boolean> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store[account.id] = account
        return DomainResult.Success(true)
    }

    override suspend fun deleteAccount(account: Account): DomainResult<Boolean> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store.remove(account.id)
        return DomainResult.Success(true)
    }

    override suspend fun getAccountById(id: Int): DomainResult<Account> =
        store[id]?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(AppError.InternalError("not found"))

    override fun getAccounts(): DomainResult<Flow<List<Account>>> = DomainResult.Success(flowOf(store.values.toList()))

    override suspend fun setDefaultAccount(accountId: Int): DomainResult<Boolean> = DomainResult.Success(true)

    override fun getDefaultAccount(): DomainResult<Flow<Account?>> = DomainResult.Success(flowOf(null))

    override suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> = DomainResult.Success(null)

    override suspend fun getRemoteIdByLocalId(localId: Int): DomainResult<Long?> = DomainResult.Success(null)

    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> = DomainResult.Success(emptyList())
}

// ---------------------------------------------------------------------------
// Category
// ---------------------------------------------------------------------------

class FakeCategoryRepository(
    var shouldFail: Boolean = false,
) : CategoryRepository {
    private var seq = 1
    val store = mutableMapOf<Int, Category>()

    override suspend fun createCategory(category: Category): DomainResult<Int> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        val id = seq++
        store[id] = category.copy(id = id)
        return DomainResult.Success(id)
    }

    override suspend fun updateCategory(category: Category): DomainResult<Boolean> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store[category.id] = category
        return DomainResult.Success(true)
    }

    override suspend fun deleteCategory(category: Category): DomainResult<Boolean> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store.remove(category.id)
        return DomainResult.Success(true)
    }

    override fun getAllCategories(): DomainResult<Flow<List<Category>>> =
        DomainResult.Success(flowOf(store.values.toList()))

    override suspend fun getCategoryById(id: Int): DomainResult<Category> =
        store[id]?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(AppError.InternalError("not found"))

    override fun getAllCategoryIcons(): DomainResult<Flow<List<Any>>> = DomainResult.Success(flowOf(emptyList()))

    override fun getExpenseCategoriesByUsage(limit: Int): DomainResult<Flow<List<Category>>> =
        DomainResult.Success(flowOf(emptyList()))

    override suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> = DomainResult.Success(null)

    override suspend fun getRemoteIdByLocalId(localId: Int): DomainResult<Long?> = DomainResult.Success(null)

    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> = DomainResult.Success(emptyList())

    override suspend fun getLocalIdByResourceKey(resourceKey: String): DomainResult<Int?> = DomainResult.Success(null)
}

// ---------------------------------------------------------------------------
// Transaction
// ---------------------------------------------------------------------------

class FakeTransactionRepository(
    var shouldFail: Boolean = false,
) : TransactionRepository {
    private var seq = 1
    val store = mutableMapOf<Int, Transaction>()

    override suspend fun createTransaction(transaction: Transaction): DomainResult<Int> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        val id = seq++
        store[id] = transaction
        return DomainResult.Success(id)
    }

    override suspend fun deleteTransaction(transactionId: Long): DomainResult<Boolean> =
        DomainResult.Success(store.remove(transactionId.toInt()) != null)

    override suspend fun <T : Transaction> getAllTransactionsOfType(type: KClass<T>): DomainResult<Flow<List<T>>> =
        @Suppress("UNCHECKED_CAST")
        DomainResult.Success(flowOf(store.values as List<T>))

    override suspend fun <T : Transaction> getTransactionById(id: Int): DomainResult<T> =
        @Suppress("UNCHECKED_CAST")
        store[id]?.let { DomainResult.Success(it as T) }
            ?: DomainResult.Failure(AppError.InternalError("not found"))

    override suspend fun <T : Transaction> getMinTransactionOfType(type: KClass<T>): DomainResult<T> =
        DomainResult.Failure(AppError.InternalError("none"))

    override suspend fun <T : Transaction> getMaxTransactionOfType(type: KClass<T>): DomainResult<T> =
        DomainResult.Failure(AppError.InternalError("none"))

    override suspend fun <T : Transaction> getAverageTransactionValueOfType(type: KClass<T>): DomainResult<Double> =
        DomainResult.Success(0.0)

    override suspend fun getFilteredTransactions(filter: TransactionFilter): DomainResult<List<Transaction>> =
        DomainResult.Success(store.values.toList())

    override suspend fun updateTransaction(newTransaction: Transaction): DomainResult<Boolean> {
        store[newTransaction.id] = newTransaction
        return DomainResult.Success(true)
    }

    override suspend fun <T : Transaction> getTotalTransactionsOfTypeAndCurrency(
        currency: Currency,
        type: KClass<T>,
    ): DomainResult<Flow<Double>> = DomainResult.Success(flowOf(0.0))

    override suspend fun <T : Transaction> getTotalTransactionsOfTypesAndCategories(
        type: KClass<T>,
    ): DomainResult<Flow<Map<Category, Double>>> = DomainResult.Success(flowOf(emptyMap()))

    override suspend fun getAllTransactions(): DomainResult<Flow<List<Transaction>>> =
        DomainResult.Success(flowOf(store.values.toList()))

    override suspend fun getAllTransactionsPaged(page: Int): DomainResult<Flow<PagedData<Transaction>>> =
        DomainResult.Success(
            flowOf(
                PagedData(
                    store.values.toList(),
                    page,
                    20,
                    store.size.toLong(),
                    totalPages = 1,
                    hasNextPage = false,
                    hasPreviousPage = false,
                ),
            ),
        )

    override suspend fun <T : Transaction> getAllTransactionsOfTypePaged(
        type: KClass<T>,
        page: Int,
    ): DomainResult<Flow<PagedData<T>>> =
        DomainResult.Success(
            flowOf(
                PagedData(
                    emptyList(),
                    page,
                    20,
                    0L,
                    totalPages = 1,
                    hasNextPage = false,
                    hasPreviousPage = false,
                ),
            ),
        )

    override suspend fun getFilteredTransactionsPaged(
        filter: TransactionFilter,
        page: Int,
    ): DomainResult<Flow<PagedData<Transaction>>> =
        DomainResult.Success(
            flowOf(
                PagedData(
                    store.values.toList(),
                    page,
                    20,
                    store.size.toLong(),
                    totalPages = 1,
                    hasNextPage = false,
                    hasPreviousPage = false,
                ),
            ),
        )

    override suspend fun getTransactionCount(): DomainResult<Long> = DomainResult.Success(store.size.toLong())

    override suspend fun <T : Transaction> getTransactionCountOfType(type: KClass<T>): DomainResult<Long> =
        DomainResult.Success(store.values.count { type.isInstance(it) }.toLong())

    override suspend fun getFilteredTransactionCount(filter: TransactionFilter): DomainResult<Long> =
        DomainResult.Success(store.size.toLong())

    override suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> = DomainResult.Success(null)

    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> = DomainResult.Success(emptyList())
}

// ---------------------------------------------------------------------------
// SyncQueue
// ---------------------------------------------------------------------------

class FakeSyncQueueRepository : SyncQueueRepository {
    val failed = mutableListOf<SyncQueueEntry>()
    val pending = mutableListOf<SyncQueueEntry>()
    val markedFailedIds = mutableListOf<Long>()
    private var seq = 1L

    fun addPending(entityType: String = "ACCOUNT"): SyncQueueEntry {
        val entry =
            SyncQueueEntry(
                id = seq++,
                entityType = entityType,
                operation = "CREATE",
                payload = "{}",
                localId = null,
                remoteId = null,
                createdAt = 0L,
                retryCount = 0,
                status = "PENDING",
            )
        pending.add(entry)
        return entry
    }

    fun addFailed(entityType: String = "ACCOUNT"): SyncQueueEntry {
        val entry =
            SyncQueueEntry(
                id = seq++,
                entityType = entityType,
                operation = "CREATE",
                payload = "{}",
                localId = null,
                remoteId = null,
                createdAt = 0L,
                retryCount = 3,
                status = "FAILED",
            )
        failed.add(entry)
        return entry
    }

    override suspend fun insertEntry(
        entityType: String,
        operation: String,
        payload: String,
        localId: Long?,
        remoteId: Long?,
    ) {
    }

    override suspend fun getPendingEntries(): List<SyncQueueEntry> = pending.toList()

    override suspend fun markSent(
        id: Long,
        remoteId: Long?,
    ) {
    }

    override suspend fun incrementRetry(id: Long) {}

    override suspend fun markFailed(id: Long) {
        markedFailedIds.add(id)
    }

    override suspend fun deleteById(id: Long) {
        failed.removeAll { it.id == id }
    }

    override suspend fun getFailedEntries(): List<SyncQueueEntry> = failed.toList()

    override suspend fun getFailedCount(): Long = failed.size.toLong()

    override suspend fun resetToRetry(id: Long) {
        val idx = failed.indexOfFirst { it.id == id }
        if (idx >= 0) failed[idx] = failed[idx].copy(status = "PENDING")
    }

    override suspend fun deleteAllFailed() {
        failed.clear()
    }

    override suspend fun hasActiveEntry(
        localId: Long,
        entityType: String,
    ): Boolean = false
}

// ---------------------------------------------------------------------------
// User (not logged in — suppresses all sync paths)
// ---------------------------------------------------------------------------

class FakeUserRepository : UserRepository {
    var lastSyncAtSet: Long? = null
    var fcmTokenCleared = false

    override fun isLoggedIn(): Boolean = false

    override fun isOnboardingComplete(): Boolean = false

    override fun markOnboardingComplete() {}

    override fun getUserData(): DomainResult<UserData?> = DomainResult.Success(null)

    override fun upsertUserData(userData: UserData): DomainResult<Unit> = DomainResult.Success(Unit)

    override fun isDarkMode(): Boolean = false

    override fun isDynamicColors(): Boolean = false

    override fun getAppLanguage(): AppLanguage? = null

    override fun toggleDarkMode() {}

    override fun toggleDynamicColors() {}

    override fun setAppLanguage(appLanguage: AppLanguage) {}

    override fun getUserSavedColors(): List<String> = emptyList()

    override fun saveColor(color: String) {}

    override fun isDataSeeded(): Boolean = false

    override fun markDataAsSeeded() {}

    override fun isBiometricEnabled(): Boolean = false

    override fun setBiometricEnabled(enabled: Boolean) {}

    override fun hasShownBiometricSuggestion(): Boolean = false

    override fun markBiometricSuggestionShown() {}

    override fun isAutoSyncRatesEnabled(): Boolean = false

    override fun setAutoSyncRatesEnabled(enabled: Boolean) {}

    override fun isAutoSyncDataEnabled(): Boolean = false

    override fun setAutoSyncDataEnabled(enabled: Boolean) {}

    override fun getLastSyncAt(): Long = 0L

    override fun setLastSyncAt(ts: Long) {
        lastSyncAtSet = ts
    }

    override fun syncFcmToken(
        token: String,
        platform: String,
    ) {
    }

    override fun getFcmToken(): String? = null

    override fun getFcmPlatform(): String? = null

    override fun clearFcmToken() {
        fcmTokenCleared = true
    }
}

// ---------------------------------------------------------------------------
// Sync (no-op remote)
// ---------------------------------------------------------------------------

class FakeSyncRepository(
    var shouldConflict: Boolean = false,
    var shouldUpgradeRequired: Boolean = false,
) : SyncRepository {
    var enqueuedCount = 0

    override suspend fun enqueueRemote(entry: SyncQueueEntry): DomainResult<Unit> {
        enqueuedCount++
        return when {
            shouldConflict -> DomainResult.Failure(AppError.ConflictError("conflict"))
            shouldUpgradeRequired -> DomainResult.Failure(AppError.UpgradeRequired("upgrade required"))
            else -> DomainResult.Success(Unit)
        }
    }

    override suspend fun pullDelta(since: Long): DomainResult<SyncPullResponse> =
        if (shouldUpgradeRequired) {
            DomainResult.Failure(AppError.UpgradeRequired("upgrade required"))
        } else {
            DomainResult.Success(
                SyncPullResponse(
                    accounts = emptyList(),
                    transactions = emptyList(),
                    categories = emptyList(),
                    currencies = emptyList(),
                    financialSessions = emptyList(),
                    attachments = emptyList(),
                ),
            )
        }

    override suspend fun downloadAttachment(remoteId: Long): DomainResult<ByteArray> =
        DomainResult.Failure(AppError.InternalError("not supported"))
}

// ---------------------------------------------------------------------------
// Minimal stubs for deps wired into SyncEnqueueHelper but never called
// (user is not logged in → sync is fully bypassed)
// ---------------------------------------------------------------------------

class FakeCurrencyRepository(
    var shouldFail: Boolean = false,
) : CurrencyRepository {
    private var seq = 1
    val store = mutableMapOf<Int, Currency>()

    override suspend fun createCurrency(currency: Currency): DomainResult<Int> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        val id = seq++
        store[id] = currency.copy(id = id)
        return DomainResult.Success(id)
    }

    override suspend fun updateCurrency(currency: Currency): DomainResult<Boolean> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store[currency.id] = currency
        return DomainResult.Success(true)
    }

    override suspend fun deleteCurrency(currency: Currency): DomainResult<Boolean> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store.remove(currency.id)
        return DomainResult.Success(true)
    }

    override suspend fun getCurrencyById(id: Int): DomainResult<Currency> =
        store[id]?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(AppError.InternalError("none"))

    override fun getAllCurrencies(): DomainResult<Flow<List<Currency>>> =
        DomainResult.Success(flowOf(store.values.toList()))

    override suspend fun getUsedCurrencies(): DomainResult<Flow<List<Currency>>> =
        DomainResult.Success(flowOf(store.values.toList()))

    override suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> = DomainResult.Success(null)

    override suspend fun getRemoteIdByLocalId(localId: Int): DomainResult<Long?> = DomainResult.Success(null)

    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> = DomainResult.Success(emptyList())

    override suspend fun getLocalIdByResourceKey(resourceKey: String): DomainResult<Int?> = DomainResult.Success(null)
}

class FakeFinancialSessionRepository : FinancialSessionRepository {
    override suspend fun createSession(session: FinancialSession): DomainResult<Int> = DomainResult.Success(1)

    override suspend fun updateSession(session: FinancialSession): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun deleteSession(sessionId: Int): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun getAllSessions(): DomainResult<Flow<List<FinancialSession>>> =
        DomainResult.Success(flowOf(emptyList()))

    override suspend fun getSessionById(id: Int): DomainResult<FinancialSession> =
        DomainResult.Failure(AppError.InternalError("none"))

    override suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> = DomainResult.Success(null)

    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> = DomainResult.Success(emptyList())
}

class FakeAttachmentRepository : AttachmentRepository {
    override suspend fun createAttachment(attachment: Attachment): DomainResult<Int> = DomainResult.Success(1)

    override suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> = DomainResult.Success(emptyList())

    override suspend fun getByRemoteId(remoteId: Long): DomainResult<Attachment?> = DomainResult.Success(null)

    override suspend fun getAttachmentsOfEntityType(type: AttachmentEntityType): DomainResult<Flow<List<Attachment>>> =
        DomainResult.Success(flowOf(emptyList()))

    override suspend fun deleteAttachment(id: Int): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun getAttachmentsByEntity(
        entityType: AttachmentEntityType,
        entityId: Int,
    ): DomainResult<List<Attachment>> = DomainResult.Success(emptyList())

    override suspend fun deleteAttachmentsByEntity(
        entityType: AttachmentEntityType,
        entityId: Int,
    ): DomainResult<Boolean> = DomainResult.Success(true)
}

// ---------------------------------------------------------------------------
// Auth
// ---------------------------------------------------------------------------

@Suppress("TooManyFunctions")
class FakeAuthRepository(
    private val shouldFail: Boolean = false,
    private val authenticated: Boolean = false,
    private val emailVerified: Boolean = false,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): DomainResult<Unit> =
        if (shouldFail) DomainResult.Failure(AppError.InternalError("fail")) else DomainResult.Success(Unit)

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): DomainResult<Unit> =
        if (shouldFail) DomainResult.Failure(AppError.InternalError("fail")) else DomainResult.Success(Unit)

    override suspend fun logout(): DomainResult<Unit> =
        if (shouldFail) DomainResult.Failure(AppError.InternalError("fail")) else DomainResult.Success(Unit)

    override suspend fun logoutAll(): DomainResult<Unit> =
        if (shouldFail) DomainResult.Failure(AppError.InternalError("fail")) else DomainResult.Success(Unit)

    override suspend fun refreshTokens(): DomainResult<Unit> = DomainResult.Success(Unit)

    override fun isAuthenticated(): Boolean = authenticated

    override fun isEmailVerified(): Boolean = emailVerified

    override suspend fun verifyEmail(token: String): DomainResult<Unit> =
        if (shouldFail) DomainResult.Failure(AppError.InternalError("fail")) else DomainResult.Success(Unit)

    override suspend fun resendVerification(): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun forgotPassword(email: String): DomainResult<Unit> =
        if (shouldFail) DomainResult.Failure(AppError.InternalError("fail")) else DomainResult.Success(Unit)

    override suspend fun resetPassword(
        token: String,
        newPassword: String,
    ): DomainResult<Unit> =
        if (shouldFail) DomainResult.Failure(AppError.InternalError("fail")) else DomainResult.Success(Unit)
}

// ---------------------------------------------------------------------------
// CurrencyExchangeRate
// ---------------------------------------------------------------------------

class FakeCurrencyExchangeRateRepository(
    private val shouldFail: Boolean = false,
) : CurrencyExchangeRateRepository {
    private var seq = 1
    val store = mutableListOf<CurrencyExchangeRate>()

    override suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Int> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store.add(rate)
        return DomainResult.Success(seq++)
    }

    override suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>): DomainResult<Boolean> =
        DomainResult.Success(true)

    override suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Boolean> {
        store.remove(rate)
        return DomainResult.Success(true)
    }

    override suspend fun getCurrencyExchangeRateById(
        id: Int,
        toId: Int,
    ): DomainResult<CurrencyExchangeRate> =
        store
            .find { it.from.id == id && it.to.id == toId }
            ?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(AppError.InternalError("not found"))

    override fun getExchangeRatesOfCurrency(currency: Currency): DomainResult<Flow<List<CurrencyExchangeRate>>> =
        DomainResult.Success(flowOf(store.filter { it.from.id == currency.id }))

    override fun getAllCurrenciesExchangeRates(): DomainResult<Flow<List<CurrencyExchangeRate>>> =
        DomainResult.Success(flowOf(store.toList()))
}

// ---------------------------------------------------------------------------
// Backup
// ---------------------------------------------------------------------------

class FakeBackupRepository(
    private val shouldFail: Boolean = false,
) : BackupRepository {
    var cleared = false
    val importedData = mutableListOf<ByteArray>()

    override suspend fun exportDatabase(): DomainResult<ByteArray> =
        if (shouldFail) {
            DomainResult.Failure(AppError.InternalError("fail"))
        } else {
            DomainResult.Success(ByteArray(4) { it.toByte() })
        }

    override suspend fun importDatabaseClean(data: ByteArray): DomainResult<Unit> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        importedData.add(data)
        return DomainResult.Success(Unit)
    }

    override suspend fun importDatabaseAppend(data: ByteArray): DomainResult<Unit> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        importedData.add(data)
        return DomainResult.Success(Unit)
    }

    override suspend fun clearUserData(): DomainResult<Unit> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        cleared = true
        return DomainResult.Success(Unit)
    }
}

// ---------------------------------------------------------------------------
// BankName
// ---------------------------------------------------------------------------

class FakeBankNameRepository(
    var shouldFail: Boolean = false,
) : BankNameRepository {
    private var seq = 1
    val store = mutableMapOf<Int, BankName>()

    override suspend fun createBankName(bankName: BankName): DomainResult<Int> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        val id = seq++
        store[id] = bankName.copy(id = id)
        return DomainResult.Success(id)
    }

    override suspend fun updateBankName(bankName: BankName): DomainResult<Boolean> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store[bankName.id] = bankName
        return DomainResult.Success(true)
    }

    override suspend fun deleteBankName(bankName: BankName): DomainResult<Boolean> {
        if (shouldFail) return DomainResult.Failure(AppError.InternalError("fail"))
        store.remove(bankName.id)
        return DomainResult.Success(true)
    }

    override suspend fun getBankNameById(id: Int): DomainResult<BankName> =
        store[id]?.let { DomainResult.Success(it) }
            ?: DomainResult.Failure(AppError.InternalError("not found"))

    override fun getAllBankNames(): DomainResult<Flow<List<BankName>>> =
        DomainResult.Success(flowOf(store.values.toList()))
}

// ---------------------------------------------------------------------------
// Subscription
// ---------------------------------------------------------------------------

class FakeSubscriptionRepository(
    private val isProActive: Boolean = false,
    private val shouldFail: Boolean = false,
) : SubscriptionRepository {
    var linkCallCount = 0

    override fun getStatus(): Flow<DomainResult<SubscriptionStatus>> =
        flowOf(
            if (shouldFail) {
                DomainResult.Failure(AppError.InternalError("fail"))
            } else {
                DomainResult.Success(
                    if (isProActive) {
                        SubscriptionStatus(
                            plan = SubscriptionPlan.PRO,
                            status = SubscriptionStatusType.ACTIVE,
                            expiresAt = null,
                        )
                    } else {
                        SubscriptionStatus.FREE_DEFAULT
                    },
                )
            },
        )

    override suspend fun linkRevenueCatCustomer(rcCustomerId: String): DomainResult<Unit> {
        linkCallCount++
        return if (shouldFail) {
            DomainResult.Failure(AppError.InternalError("fail"))
        } else {
            DomainResult.Success(Unit)
        }
    }

    override suspend fun getCachedStatusAge(): Long = 0L
}

class FakeRevenueCatSdk(
    private val shouldSucceed: Boolean = true,
    private val shouldCancel: Boolean = false,
) : RevenueCatSdk {
    var initializeCallCount = 0
    var lastInitializedUserId: String? = null

    override suspend fun purchasePro(): DomainResult<String> =
        when {
            shouldCancel -> DomainResult.Failure(AppError.InternalError("cancelled"))
            shouldSucceed -> DomainResult.Success("rc_customer_123")
            else -> DomainResult.Failure(AppError.InternalError("purchase_failed"))
        }

    override suspend fun restorePurchases(): DomainResult<String> =
        if (shouldSucceed) {
            DomainResult.Success("rc_customer_123")
        } else {
            DomainResult.Failure(AppError.InternalError("restore_failed"))
        }

    override suspend fun getCustomerInfo(): DomainResult<SubscriptionStatus> =
        DomainResult.Success(SubscriptionStatus.FREE_DEFAULT)

    override fun initialize(
        apiKey: String,
        userId: String?,
    ) {
        initializeCallCount++
        lastInitializedUserId = userId
    }
}
