package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.error.AppError
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_accounts

class AccountRepositoryImpl(
    private val database: SharedDatabase
) : AccountRepository {

    override suspend fun createAccount(account: Account): DomainResult<Int> {
        return try {
            val result = database {
                it.bankAccountsQueries.insertBankAccount(
                    currency = account.currency.id.toLong(),
                    name = account.name,
                    description = account.description,
                    balance = account.balance,
                    color = account.color,
                    logo = account.logo,
                )
            }
            DomainResult.Success(result.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating account"))
        }
    }

    override suspend fun updateAccount(account: Account): DomainResult<Boolean> {
        return try {
            val result = database {
                it.bankAccountsQueries.updateAccount(
                    balance = account.balance,
                    name = account.name,
                    color = account.color,
                    logo = account.logo,
                    id = account.id.toLong()
                )
            }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating account"))
        }
    }

    override suspend fun deleteAccount(account: Account): DomainResult<Boolean> {
        return try {
            val result = database { it.bankAccountsQueries.deleteAccount(account.id.toLong()) }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting account"))
        }
    }

    override suspend fun getAccountById(id: Int): DomainResult<Account> {
        return try {
            val account = database {
                it.bankAccountsQueries.getAccountById(id.toLong()).awaitAsOne()
            }
            DomainResult.Success(account.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting account"))
        }
    }

    override fun getAccounts(): DomainResult<Flow<List<Account>>> {
        return try {
            val accountsFlow: Flow<List<Account>> = flow {
                val accountsFlow = database { db ->
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
    }

    private fun V_accounts.toDomain(): Account {
        return Account(
            id = accountId.toInt(),
            name = accountName,
            description = accountDescription,
            balance = accountBalance,
            color = accountColor,
            logo = accountLogo.toString(),
            currency = Currency(
                name = currencyName,
                sign = currencySign,
                id = currencyId.toInt()
            )
        )
    }

}
