package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.sync.AccountSyncPayload
import tech.lightfeather.domain.repository.AccountRepository
import tech.lightfeather.domain.repository.CurrencyRepository

class CreateAccount(
    private val accountRepository: AccountRepository,
    private val syncHelper: SyncEnqueueHelper,
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(account: Account) =
        accountRepository.createAccount(account).also { result ->
            result.getOrNull()?.let { newId ->
                val payload = account.copy(id = newId).toSyncPayload(currencyRepository)
                if (payload != null) {
                    syncHelper.enqueue("ACCOUNT", "CREATE", payload, AccountSyncPayload.serializer(), newId)
                } else if (syncHelper.isLoggedIn()) {
                    syncHelper.fullSync()
                }
            }
        }
}

class UpdateAccount(
    private val accountRepository: AccountRepository,
    private val syncHelper: SyncEnqueueHelper,
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(account: Account) =
        accountRepository.updateAccount(account).also { result ->
            if (result.isSuccess) {
                val payload = account.toSyncPayload(currencyRepository)
                if (payload != null) {
                    syncHelper.enqueue("ACCOUNT", "UPDATE", payload, AccountSyncPayload.serializer(), account.id)
                } else if (syncHelper.isLoggedIn()) {
                    syncHelper.fullSync()
                }
            }
        }
}

class DeleteAccount(
    private val accountRepository: AccountRepository,
    private val syncHelper: SyncEnqueueHelper,
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(account: Account) =
        accountRepository.deleteAccount(account).also { result ->
            if (result.isSuccess) {
                val payload = account.toSyncPayload(currencyRepository)
                if (payload != null) {
                    syncHelper.enqueue("ACCOUNT", "DELETE", payload, AccountSyncPayload.serializer(), account.id)
                } else if (syncHelper.isLoggedIn()) {
                    syncHelper.fullSync()
                }
            }
        }
}

class GetAllAccounts(
    private val repository: AccountRepository,
) {
    operator fun invoke() = repository.getAccounts()
}

class GetBankAccountById(
    private val repository: AccountRepository,
) {
    suspend operator fun invoke(accountId: Int) = repository.getAccountById(accountId)
}

class SetDefaultAccount(
    private val repository: AccountRepository,
) {
    suspend operator fun invoke(accountId: Int) = repository.setDefaultAccount(accountId)
}

class GetDefaultAccount(
    private val repository: AccountRepository,
) {
    operator fun invoke() = repository.getDefaultAccount()
}

internal suspend fun Account.toSyncPayload(currencyRepository: CurrencyRepository): AccountSyncPayload? {
    val currencyId = currencyRepository.getRemoteIdByLocalId(currency.id).getOrNull() ?: return null
    return AccountSyncPayload(
        name = name,
        description = description,
        balance = balance,
        currencyId = currencyId,
        color = color,
        logo = logo,
        isDefault = isDefault,
    )
}
