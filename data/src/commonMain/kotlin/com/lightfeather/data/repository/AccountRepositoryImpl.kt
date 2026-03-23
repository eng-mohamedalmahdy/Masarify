package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.error.AppError
import com.lightfeather.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_accounts

class AccountRepositoryImpl(
    private val database: SharedDatabase,
) : AccountRepository {
    // DomainResult pattern requires catching all exceptions for proper error handling
    @Suppress("TooGenericExceptionCaught")
    override suspend fun createAccount(account: Account): DomainResult<Int> =
        try {
            val result =
                database {
                    val bankAccountsQueries = it.bankAccountsQueries
                    bankAccountsQueries.transactionWithResult {
                        bankAccountsQueries.insertBankAccount(
                            currency = account.currency.id.toLong(),
                            name = account.name,
                            description = account.description,
                            balance = account.balance,
                            color = account.color,
                            logo = account.logo,
                            isDefault = if (account.isDefault) 1L else 0L,
                        )
                        bankAccountsQueries.selectLastInsertedRowId().awaitAsOne()
                    }
                }
            DomainResult.Success(result.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating account"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateAccount(account: Account): DomainResult<Boolean> =
        try {
            val result =
                database {
                    it.bankAccountsQueries.updateAccount(
                        balance = account.balance,
                        name = account.name,
                        color = account.color,
                        logo = account.logo,
                        isDefault = if (account.isDefault) 1L else 0L,
                        id = account.id.toLong(),
                    )
                }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating account"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun deleteAccount(account: Account): DomainResult<Boolean> =
        try {
            val result = database { it.bankAccountsQueries.deleteAccount(account.id.toLong()) }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting account"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getAccountById(id: Int): DomainResult<Account> =
        try {
            val account =
                database {
                    it.bankAccountsQueries.getAccountById(id.toLong()).awaitAsOne()
                }
            DomainResult.Success(account.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting account"))
        }

    @Suppress("TooGenericExceptionCaught")
    override fun getAccounts(): DomainResult<Flow<List<Account>>> =
        try {
            val accountsFlow: Flow<List<Account>> =
                flow {
                    val accountsFlow =
                        database { db ->
                            db.bankAccountsQueries
                                .getAllAccounts()
                                .asFlow()
                                .map { query -> query.awaitAsList().map { it.toDomain() } }
                        }
                    emitAll(accountsFlow)
                }

            DomainResult.Success(accountsFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting accounts"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun setDefaultAccount(accountId: Int): DomainResult<Boolean> =
        try {
            database {
                val queries = it.bankAccountsQueries
                queries.transaction {
                    queries.clearDefaultAccounts()
                    queries.markAccountAsDefault(accountId.toLong())
                }
            }
            DomainResult.Success(true)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error setting default account"))
        }

    @Suppress("TooGenericExceptionCaught")
    override fun getDefaultAccount(): DomainResult<Flow<Account?>> =
        try {
            val defaultAccountFlow: Flow<Account?> =
                flow {
                    val accountFlow =
                        database { db ->
                            db.bankAccountsQueries
                                .getDefaultAccount()
                                .asFlow()
                                .map { query -> query.awaitAsOneOrNull()?.toDomain() }
                        }
                    emitAll(accountFlow)
                }
            DomainResult.Success(defaultAccountFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting default account"))
        }

    private fun V_accounts.toDomain(): Account =
        Account(
            id = accountId.toInt(),
            name = accountName,
            description = accountDescription,
            balance = accountBalance,
            color = accountColor,
            logo = accountLogo.toString(),
            isDefault = accountIsDefault == 1L,
            currency =
                Currency(
                    name = currencyName,
                    sign = currencySign,
                    id = currencyId.toInt(),
                ),
        )
}
