@file:Suppress("EmptyFunctionBlock", "TooManyFunctions")

package tech.lightfeather.masarify.test

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
import tech.lightfeather.domain.repository.DeviceTokenRepository
import tech.lightfeather.domain.repository.FCMHelper
import tech.lightfeather.domain.repository.FinancialSessionRepository
import tech.lightfeather.domain.repository.Platform
import tech.lightfeather.domain.repository.RevenueCatSdk
import tech.lightfeather.domain.repository.SubscriptionRepository
import tech.lightfeather.domain.repository.SyncQueueRepository
import tech.lightfeather.domain.repository.SyncRepository
import tech.lightfeather.domain.repository.TransactionRepository
import tech.lightfeather.domain.repository.UserRepository
import kotlin.reflect.KClass

// ---------------------------------------------------------------------------
// Auth
// ---------------------------------------------------------------------------

@Suppress("TooManyFunctions")
internal class FakeAuthRepository(
    private val shouldLoginSucceed: Boolean = true,
    private val shouldRegisterSucceed: Boolean = true,
    private val shouldForgotPasswordSucceed: Boolean = true,
    private val shouldVerifyEmailSucceed: Boolean = true,
    private val shouldResetPasswordSucceed: Boolean = true,
) : AuthRepository {
    var loginCallCount = 0
    var registerCallCount = 0
    var forgotPasswordCallCount = 0
    var verifyEmailCallCount = 0
    var resetPasswordCallCount = 0

    override suspend fun login(
        email: String,
        password: String,
    ): DomainResult<Unit> {
        loginCallCount++
        return if (shouldLoginSucceed) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Failure(AppError.InternalError("auth_failed"))
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): DomainResult<Unit> {
        registerCallCount++
        return if (shouldRegisterSucceed) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Failure(AppError.InternalError("register_failed"))
        }
    }

    override suspend fun logout(): DomainResult<Unit> = TODO()

    override suspend fun logoutAll(): DomainResult<Unit> = TODO()

    override suspend fun refreshTokens(): DomainResult<Unit> = TODO()

    override fun isAuthenticated(): Boolean = false

    override fun isEmailVerified(): Boolean = false

    override suspend fun verifyEmail(token: String): DomainResult<Unit> {
        verifyEmailCallCount++
        return if (shouldVerifyEmailSucceed) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Failure(AppError.InternalError("verify_failed"))
        }
    }

    override suspend fun resendVerification(): DomainResult<Unit> = TODO()

    override suspend fun forgotPassword(email: String): DomainResult<Unit> {
        forgotPasswordCallCount++
        return if (shouldForgotPasswordSucceed) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Failure(AppError.InternalError("forgot_password_failed"))
        }
    }

    override suspend fun resetPassword(
        token: String,
        newPassword: String,
    ): DomainResult<Unit> {
        resetPasswordCallCount++
        return if (shouldResetPasswordSucceed) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Failure(AppError.InternalError("reset_failed"))
        }
    }
}

// ---------------------------------------------------------------------------
// User
// ---------------------------------------------------------------------------

@Suppress("LargeClass")
internal class FakeUserRepository(
    private val isLoggedInValue: Boolean = false,
    private val isOnboardingCompleteValue: Boolean = false,
    private val isDataSeededValue: Boolean = false,
) : UserRepository {
    override fun isLoggedIn(): Boolean = isLoggedInValue

    override fun isOnboardingComplete(): Boolean = isOnboardingCompleteValue

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

    override fun isDataSeeded(): Boolean = isDataSeededValue

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

    override fun setLastSyncAt(ts: Long) {}

    override fun syncFcmToken(
        token: String,
        platform: String,
    ) {}

    override fun getFcmToken(): String? = null

    override fun getFcmPlatform(): String? = null

    override fun clearFcmToken() {}
}

// ---------------------------------------------------------------------------
// Sync
// ---------------------------------------------------------------------------

internal class FakeSyncRepository : SyncRepository {
    override suspend fun enqueueRemote(entry: SyncQueueEntry): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun pullDelta(since: Long): DomainResult<SyncPullResponse> =
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

    override suspend fun downloadAttachment(remoteId: Long): DomainResult<ByteArray> =
        DomainResult.Failure(AppError.InternalError("not supported"))
}

// ---------------------------------------------------------------------------
// SyncQueue
// ---------------------------------------------------------------------------

internal class FakeSyncQueueRepository : SyncQueueRepository {
    override suspend fun insertEntry(
        entityType: String,
        operation: String,
        payload: String,
        localId: Long?,
        remoteId: Long?,
    ) {}

    override suspend fun getPendingEntries(): List<SyncQueueEntry> = emptyList()

    override suspend fun markSent(
        id: Long,
        remoteId: Long?,
    ) {}

    override suspend fun incrementRetry(id: Long) {}

    override suspend fun markFailed(id: Long) {}

    override suspend fun deleteById(id: Long) {}

    override suspend fun getFailedEntries(): List<SyncQueueEntry> = emptyList()

    override suspend fun getFailedCount(): Long = 0L

    override suspend fun resetToRetry(id: Long) {}

    override suspend fun deleteAllFailed() {}

    override suspend fun hasActiveEntry(
        localId: Long,
        entityType: String,
    ): Boolean = false
}

// ---------------------------------------------------------------------------
// Account
// ---------------------------------------------------------------------------

internal class FakeAccountRepository : AccountRepository {
    override suspend fun createAccount(account: Account): DomainResult<Int> = DomainResult.Success(1)

    override suspend fun updateAccount(account: Account): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun deleteAccount(account: Account): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun getAccountById(id: Int): DomainResult<Account> =
        DomainResult.Failure(AppError.InternalError("none"))

    override fun getAccounts(): DomainResult<Flow<List<Account>>> = DomainResult.Success(flowOf(emptyList()))

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
// Transaction
// ---------------------------------------------------------------------------

internal class FakeTransactionRepository : TransactionRepository {
    override suspend fun createTransaction(transaction: Transaction): DomainResult<Int> = DomainResult.Success(1)

    override suspend fun deleteTransaction(transactionId: Long): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun <T : Transaction> getAllTransactionsOfType(type: KClass<T>): DomainResult<Flow<List<T>>> =
        DomainResult.Success(flowOf(emptyList()))

    override suspend fun <T : Transaction> getTransactionById(id: Int): DomainResult<T> =
        DomainResult.Failure(AppError.InternalError("none"))

    override suspend fun <T : Transaction> getMinTransactionOfType(type: KClass<T>): DomainResult<T> =
        DomainResult.Failure(AppError.InternalError("none"))

    override suspend fun <T : Transaction> getMaxTransactionOfType(type: KClass<T>): DomainResult<T> =
        DomainResult.Failure(AppError.InternalError("none"))

    override suspend fun <T : Transaction> getAverageTransactionValueOfType(type: KClass<T>): DomainResult<Double> =
        DomainResult.Success(0.0)

    override suspend fun getFilteredTransactions(filter: TransactionFilter): DomainResult<List<Transaction>> =
        DomainResult.Success(emptyList())

    override suspend fun updateTransaction(newTransaction: Transaction): DomainResult<Boolean> =
        DomainResult.Success(true)

    override suspend fun <T : Transaction> getTotalTransactionsOfTypeAndCurrency(
        currency: Currency,
        type: KClass<T>,
    ): DomainResult<Flow<Double>> = DomainResult.Success(flowOf(0.0))

    override suspend fun <T : Transaction> getTotalTransactionsOfTypesAndCategories(
        type: KClass<T>,
    ): DomainResult<Flow<Map<Category, Double>>> = DomainResult.Success(flowOf(emptyMap()))

    override suspend fun getAllTransactions(): DomainResult<Flow<List<Transaction>>> =
        DomainResult.Success(flowOf(emptyList()))

    override suspend fun getAllTransactionsPaged(page: Int): DomainResult<Flow<PagedData<Transaction>>> =
        DomainResult.Success(flowOf(PagedData.create(emptyList(), page, 20, 0L)))

    override suspend fun <T : Transaction> getAllTransactionsOfTypePaged(
        type: KClass<T>,
        page: Int,
    ): DomainResult<Flow<PagedData<T>>> = DomainResult.Success(flowOf(PagedData.create(emptyList(), page, 20, 0L)))

    override suspend fun getFilteredTransactionsPaged(
        filter: TransactionFilter,
        page: Int,
    ): DomainResult<Flow<PagedData<Transaction>>> =
        DomainResult.Success(flowOf(PagedData.create(emptyList(), page, 20, 0L)))

    override suspend fun getTransactionCount(): DomainResult<Long> = DomainResult.Success(0L)

    override suspend fun <T : Transaction> getTransactionCountOfType(type: KClass<T>): DomainResult<Long> =
        DomainResult.Success(0L)

    override suspend fun getFilteredTransactionCount(filter: TransactionFilter): DomainResult<Long> =
        DomainResult.Success(0L)

    override suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> = DomainResult.Success(null)

    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> = DomainResult.Success(emptyList())
}

// ---------------------------------------------------------------------------
// Category
// ---------------------------------------------------------------------------

internal class FakeCategoryRepository : CategoryRepository {
    override suspend fun createCategory(category: Category): DomainResult<Int> = DomainResult.Success(1)

    override suspend fun updateCategory(category: Category): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun deleteCategory(category: Category): DomainResult<Boolean> = DomainResult.Success(true)

    override fun getAllCategories(): DomainResult<Flow<List<Category>>> = DomainResult.Success(flowOf(emptyList()))

    override suspend fun getCategoryById(id: Int): DomainResult<Category> =
        DomainResult.Failure(AppError.InternalError("none"))

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
// Currency
// ---------------------------------------------------------------------------

internal class FakeCurrencyRepository : CurrencyRepository {
    override suspend fun createCurrency(currency: Currency): DomainResult<Int> = DomainResult.Success(1)

    override suspend fun updateCurrency(currency: Currency): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun deleteCurrency(currency: Currency): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun getCurrencyById(id: Int): DomainResult<Currency> =
        DomainResult.Failure(AppError.InternalError("none"))

    override fun getAllCurrencies(): DomainResult<Flow<List<Currency>>> = DomainResult.Success(flowOf(emptyList()))

    override suspend fun getUsedCurrencies(): DomainResult<Flow<List<Currency>>> =
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
// FinancialSession
// ---------------------------------------------------------------------------

internal class FakeFinancialSessionRepository : FinancialSessionRepository {
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

// ---------------------------------------------------------------------------
// Attachment
// ---------------------------------------------------------------------------

internal class FakeAttachmentRepository : AttachmentRepository {
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
// Backup
// ---------------------------------------------------------------------------

internal class FakeBackupRepository : BackupRepository {
    override suspend fun exportDatabase(): DomainResult<ByteArray> = DomainResult.Success(ByteArray(0))

    override suspend fun importDatabaseClean(data: ByteArray): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun importDatabaseAppend(data: ByteArray): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun clearUserData(): DomainResult<Unit> = DomainResult.Success(Unit)
}

// ---------------------------------------------------------------------------
// DeviceToken
// ---------------------------------------------------------------------------

internal class FakeDeviceTokenRepository : DeviceTokenRepository {
    override suspend fun register(
        token: String,
        platform: String,
    ): DomainResult<Unit> = DomainResult.Success(Unit)
}

// ---------------------------------------------------------------------------
// Platform + FCMHelper stubs
// ---------------------------------------------------------------------------

internal object FakePlatform : Platform {
    override val name: String = "Test"
    override val slug: String = "test"
}

internal object FakeFCMHelper : FCMHelper {
    override fun getFirebaseToken(): String? = null
}

// ---------------------------------------------------------------------------
// BankName
// ---------------------------------------------------------------------------

internal class FakeBankNameRepository : BankNameRepository {
    override suspend fun createBankName(bankName: BankName): DomainResult<Int> = DomainResult.Success(1)

    override suspend fun updateBankName(bankName: BankName): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun deleteBankName(bankName: BankName): DomainResult<Boolean> = DomainResult.Success(true)

    override suspend fun getBankNameById(id: Int): DomainResult<BankName> =
        DomainResult.Failure(AppError.InternalError("none"))

    override fun getAllBankNames(): DomainResult<Flow<List<BankName>>> = DomainResult.Success(flowOf(emptyList()))
}

// ---------------------------------------------------------------------------
// CurrencyExchangeRate
// ---------------------------------------------------------------------------

internal class FakeCurrencyExchangeRateRepository : CurrencyExchangeRateRepository {
    override suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Int> =
        DomainResult.Success(1)

    override suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>): DomainResult<Boolean> =
        DomainResult.Success(true)

    override suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Boolean> =
        DomainResult.Success(true)

    override suspend fun getCurrencyExchangeRateById(
        id: Int,
        toId: Int,
    ): DomainResult<CurrencyExchangeRate> = DomainResult.Failure(AppError.InternalError("none"))

    override fun getExchangeRatesOfCurrency(currency: Currency): DomainResult<Flow<List<CurrencyExchangeRate>>> =
        DomainResult.Success(flowOf(emptyList()))

    override fun getAllCurrenciesExchangeRates(): DomainResult<Flow<List<CurrencyExchangeRate>>> =
        DomainResult.Success(flowOf(emptyList()))
}

// ---------------------------------------------------------------------------
// Subscription
// ---------------------------------------------------------------------------

internal class FakeSubscriptionRepository(
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

internal class FakeRevenueCatSdk(
    private val shouldSucceed: Boolean = true,
    val shouldCancel: Boolean = false,
) : RevenueCatSdk {
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
    ) {}
}
