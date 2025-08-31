package com.lightfeather.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.lightfeather.data.database.drivers.SharedDatabase
import com.lightfeather.domain.data.repository.CurrencyExchangeRateRepository
import com.lightfeather.domain.domain.Currency
import com.lightfeather.domain.domain.CurrencyExchangeRate
import com.lightfeather.domain.domain.DomainResult
import com.lightfeather.domain.domain.runCatchingDomainResult
import com.lightfeather.domain.domain.runCatchingDomainResultSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.GetAllExchangeRates
import lightfeather.masarify.database.GetExchangeRatesOfCurrency
import lightfeather.masarify.database.SelectExchangeRateById
import kotlin.math.sign

class CurrencyExchangeRateRepositoryImpl(
    private val database: SharedDatabase
) : CurrencyExchangeRateRepository {
    override suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Int> {
        return runCatchingDomainResultSuspend {
            val rowId = database {
                it.currencyExchangeRateQueries.insertExchangeRate(
                    from_currency_id = rate.from.id.toLong(),
                    to_currency_id = rate.from.id.toLong(),
                    rate = rate.rate
                )
            }.toInt()
            if (rowId > 0) {
                rowId
            } else {
                throw Exception("Failed to insert currency exchange rate")
            }
        }
    }

    override suspend fun updateCurrencyExchangeRates(
        rates: List<List<CurrencyExchangeRate>>
    ): DomainResult<Boolean> {
        return runCatchingDomainResultSuspend {
            database { db ->
                db.transaction {
                    val queries = db.currencyExchangeRateQueries
                    rates.flatten().forEach { rate ->
                        queries.insertExchangeRate(
                            from_currency_id = rate.from.id.toLong(),
                            to_currency_id = rate.to.id.toLong(),
                            rate = rate.rate
                        )
                    }
                }
                true
            }
        }
    }


    override suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Boolean> {
        return runCatchingDomainResultSuspend {
            database { db ->
                val result = db.currencyExchangeRateQueries.deleteExchangeRateById(
                    from_currency_id = rate.from.id.toLong(),
                    to_currency_id = rate.to.id.toLong()
                )
                if (result > 0) {
                    true
                } else {
                    throw Exception("Failed to delete currency exchange rate")
                }
            }
        }
    }

    override suspend fun getCurrencyExchangeRateById(
        id: Int,
        toId: Int
    ): DomainResult<CurrencyExchangeRate> {
        return runCatchingDomainResultSuspend {
            database { db ->
                db.currencyExchangeRateQueries.selectExchangeRateById(
                    from_currency_id = id.toLong(),
                    to_currency_id = toId.toLong(),
                    ::mapCurrencyExchangeRate
                ).executeAsOne()
            }
        }
    }

    override fun getExchangeRatesOfCurrency(currency: Currency): DomainResult<Flow<List<CurrencyExchangeRate>>> {
        return runCatchingDomainResult {
            flow {
                val result = database { db ->
                    db.currencyExchangeRateQueries
                        .getExchangeRatesOfCurrency(currency.id.toLong(), ::mapCurrencyExchangeRate)
                        .asFlow()
                        .mapToList(Dispatchers.Default)
                }
                emitAll(result)
            }
        }
    }

    override fun getAllCurrenciesExchangeRates(): DomainResult<Flow<List<CurrencyExchangeRate>>> {
        return runCatchingDomainResult {
            flow {
                val result = database { db ->
                    db.currencyExchangeRateQueries
                        .getAllExchangeRates(::mapCurrencyExchangeRate)
                        .asFlow()
                        .mapToList(Dispatchers.Default)
                }
                emitAll(result)
            }
        }
    }


    private fun mapCurrencyExchangeRate(
        fromCurrencyId: Long,
        fromCurrencyName: String,
        fromCurrencySign: String,
        toCurrencyId: Long,
        toCurrencyName: String,
        toCurrencySign: String,
        rate: Double?
    ): CurrencyExchangeRate {
        return CurrencyExchangeRate(
            from = Currency(fromCurrencyId.toInt(), fromCurrencyName, fromCurrencySign),
            to = Currency(toCurrencyId.toInt(), toCurrencyName, toCurrencySign),
            rate = rate ?: 1.0
        )
    }
}