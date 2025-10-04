package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.CurrencyExchangeRate
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.runCatchingDomainResult
import com.lightfeather.domain.model.runCatchingDomainResultSuspend
import com.lightfeather.domain.repository.CurrencyExchangeRateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class CurrencyExchangeRateRepositoryImpl(
    private val database: SharedDatabase,
) : CurrencyExchangeRateRepository {
    override suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Int> =
        runCatchingDomainResultSuspend {
            val rowId =
                database {
                    val currencyExchangeRateQueries = it.currencyExchangeRateQueries
                    currencyExchangeRateQueries.transactionWithResult {
                        currencyExchangeRateQueries.insertExchangeRate(
                            from_currency_id = rate.from.id.toLong(),
                            to_currency_id = rate.to.id.toLong(),
                            rate = rate.rate,
                        )
                        currencyExchangeRateQueries.selectLastInsertedRowId().awaitAsOne()
                    }
                }.toInt()
            if (rowId > 0) {
                rowId
            } else {
                -1
            }
        }

    override suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>): DomainResult<Boolean> =
        runCatchingDomainResultSuspend {
            database { db ->
                db.transaction {
                    val queries = db.currencyExchangeRateQueries
                    rates.flatten().forEach { rate ->
                        queries.updateByFrom(
                            from_currency_id = rate.from.id.toLong(),
                            to_currency_id = rate.to.id.toLong(),
                            rate = rate.rate,
                        )
                    }
                }
                true
            }
        }

    override suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Boolean> =
        runCatchingDomainResultSuspend {
            database { db ->
                val result =
                    db.currencyExchangeRateQueries.deleteExchangeRateById(
                        from_currency_id = rate.from.id.toLong(),
                        to_currency_id = rate.to.id.toLong(),
                    )
                result > 0
            }
        }

    override suspend fun getCurrencyExchangeRateById(
        id: Int,
        toId: Int,
    ): DomainResult<CurrencyExchangeRate> =
        runCatchingDomainResultSuspend {
            database { db ->
                db.currencyExchangeRateQueries
                    .selectExchangeRateById(
                        from_currency_id = id.toLong(),
                        to_currency_id = toId.toLong(),
                        ::mapCurrencyExchangeRate,
                    ).awaitAsOne()
            }
        }

    override fun getExchangeRatesOfCurrency(currency: Currency): DomainResult<Flow<List<CurrencyExchangeRate>>> =
        runCatchingDomainResult {
            flow {
                val result =
                    database { db ->
                        db.currencyExchangeRateQueries
                            .getExchangeRatesOfCurrency(currency.id.toLong(), ::mapCurrencyExchangeRate)
                            .asFlow()
                            .mapToList(Dispatchers.Default)
                    }
                emitAll(result)
            }
        }

    override fun getAllCurrenciesExchangeRates(): DomainResult<Flow<List<CurrencyExchangeRate>>> =
        runCatchingDomainResult {
            flow {
                val result =
                    database { db ->
                        db.currencyExchangeRateQueries
                            .getAllExchangeRates(::mapCurrencyExchangeRate)
                            .asFlow()
                            .mapToList(Dispatchers.Default)
                    }
                emitAll(result)
            }
        }

    private fun mapCurrencyExchangeRate(
        fromCurrencyId: Long,
        fromCurrencyName: String,
        fromCurrencySign: String,
        toCurrencyId: Long,
        toCurrencyName: String,
        toCurrencySign: String,
        rate: Double?,
    ): CurrencyExchangeRate =
        CurrencyExchangeRate(
            from = Currency(fromCurrencyName, fromCurrencySign, fromCurrencyId.toInt()),
            to = Currency(toCurrencyName, toCurrencySign, toCurrencyId.toInt()),
            rate = rate ?: 1.0,
        )
}
