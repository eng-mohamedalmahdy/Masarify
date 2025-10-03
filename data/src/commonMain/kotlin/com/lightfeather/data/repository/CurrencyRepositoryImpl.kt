package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.error.AppError
import com.lightfeather.domain.repository.CurrencyRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_currencies
import kotlin.math.sign

// DomainResult pattern requires catching all exceptions for proper error handling
@Suppress("TooGenericExceptionCaught")
class CurrencyRepositoryImpl(
    private val database: SharedDatabase,
) : CurrencyRepository {
    override suspend fun createCurrency(currency: Currency): DomainResult<Int> =
        try {
            val result =
                database {
                    val currencyQueries = it.currenciesQueries
                    currencyQueries.transactionWithResult {
                        currencyQueries.insertCurrency(
                            name = currency.name,
                            sign = currency.sign,
                        )
                        currencyQueries.selectLastInsertedRowId().awaitAsOne()
                    }
                }
            Napier.d { "CURRENCY ADDED WITH ID $result CURRENCY: $currency" }
            DomainResult.Success(result.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating currency"))
        }

    override suspend fun updateCurrency(currency: Currency): DomainResult<Boolean> =
        try {
            val result =
                database {
                    it.currenciesQueries.updateCurrency(
                        name = currency.name,
                        sign = currency.sign,
                        id = currency.id.toLong(),
                    )
                }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating currency"))
        }

    override suspend fun deleteCurrency(currency: Currency): DomainResult<Boolean> =
        try {
            val result = database { it.currenciesQueries.deleteCurrency(currency.id.toLong()) }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting currency"))
        }

    override suspend fun getCurrencyById(id: Int): DomainResult<Currency> =
        try {
            val currency =
                database {
                    it.currenciesQueries.selectCurrencyById(id.toLong()).awaitAsOne()
                }
            DomainResult.Success(currency.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting currency"))
        }

    override fun getAllCurrencies(): DomainResult<Flow<List<Currency>>> =
        try {
            val currenciesFlow: Flow<List<Currency>> =
                flow {
                    val flow =
                        database { db ->
                            db.currenciesQueries
                                .selectAllCurrencies()
                                .asFlow()
                                .map { query -> query.awaitAsList().map { it.toDomain() } }
                        }
                    emitAll(flow)
                }
            DomainResult.Success(currenciesFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting currencies"))
        }

    private fun V_currencies.toDomain(): Currency =
        Currency(
            name = currencyName,
            sign = currencySign,
            id = currencyId.toInt(),
        )
}
