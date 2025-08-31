package com.lightfeather.data.repository

import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.database.drivers.SharedDatabase
import com.lightfeather.domain.data.repository.CurrencyRepository
import com.lightfeather.domain.domain.Currency
import com.lightfeather.domain.domain.DomainResult
import com.lightfeather.domain.domain.error.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_currencies
import kotlin.math.sign

class CurrencyRepositoryImpl(
    private val database: SharedDatabase
) : CurrencyRepository {

    override suspend fun createCurrency(currency: Currency): DomainResult<Int> {
        return try {
            val result = database {
                it.currenciesQueries.insertCurrency(
                    name = currency.name,
                    sign = currency.sign
                )
                it.currenciesQueries.selectLastInsertedRowId().executeAsOne()
            }
            DomainResult.Success(result.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating currency"))
        }
    }

    override suspend fun updateCurrency(currency: Currency): DomainResult<Boolean> {
        return try {
            val result = database {
                it.currenciesQueries.updateCurrency(
                    name = currency.name,
                    sign = currency.sign,
                    id = currency.id.toLong()
                )
            }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating currency"))
        }
    }

    override suspend fun deleteCurrency(currency: Currency): DomainResult<Boolean> {
        return try {
            val result = database { it.currenciesQueries.deleteCurrency(currency.id.toLong()) }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting currency"))
        }
    }

    override suspend fun getCurrencyById(id: Int): DomainResult<Currency> {
        return try {
            val currency = database {
                it.currenciesQueries.selectCurrencyById(id.toLong()).executeAsOne()
            }
            DomainResult.Success(currency.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting currency"))
        }
    }

    override fun getAllCurrencies(): DomainResult<Flow<List<Currency>>> {
        return try {
            val currenciesFlow: Flow<List<Currency>> = flow {
                val flow = database { db ->
                    db.currenciesQueries
                        .selectAllCurrencies()
                        .asFlow()
                        .map { query -> query.executeAsList().map { it.toDomain() } }
                }
                emitAll(flow)
            }
            DomainResult.Success(currenciesFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting currencies"))
        }
    }

    private fun V_currencies.toDomain(): Currency {
        return Currency(
            id = currencyId.toInt(),
            name = currencyName,
            sign = currencySign
        )
    }
}