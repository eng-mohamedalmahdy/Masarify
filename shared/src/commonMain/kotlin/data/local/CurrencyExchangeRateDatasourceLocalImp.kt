package data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.lightfeather.core.data.datasource.CurrencyExchangeRateDatasource
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate
import com.lightfeather.masarify.database.CurrencyExchangeRateQueries
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CurrencyExchangeRateDatasourceLocalImp(private val exchangeRateQueries: CurrencyExchangeRateQueries) :
    CurrencyExchangeRateDatasource {
    override suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int {
        exchangeRateQueries.insertExchangeRate(rate.from.id.toLong(), rate.to.id.toLong(), rate.rate)
        return exchangeRateQueries.selectLastInsertedRowId().executeAsOne().toInt()
    }

    override suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>) {
        rates.forEach { rateList ->
            rateList.forEach { rate ->
                exchangeRateQueries.updateByFrom(rate.rate, rate.from.id.toLong(), rate.to.id.toLong())
            }
        }
    }

    override suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean {
        exchangeRateQueries.deleteExchangeRateById(rate.from.id.toLong(), rate.to.id.toLong())
        return true
    }

    override suspend fun getAllCurrenciesExchangeRates(): Flow<List<List<CurrencyExchangeRate>>> = flow {
        val mappedValues = exchangeRateQueries.getAllExchangeRates().executeAsList().map {
            CurrencyExchangeRate(
                Currency(
                    id = it.fromCurrencyId.toInt(),
                    name = it.fromCurrencyName,
                    sign = it.fromCurrencySign
                ), Currency(
                    id = it.toCurrencyId.toInt(),
                    name = it.toCurrencyName,
                    sign = it.toCurrencySign
                ), it.rate ?: 1.0
            )
        }

        val groupedRates = mappedValues.groupBy { it.from }

        val result = groupedRates.values.map { ratesForCurrency ->
            ratesForCurrency.sortedBy { it.to.id }
        }
        Napier.d(mappedValues.toString(), tag = "DATA")
        emit(result)
    }

    override suspend fun getCurrencyExchangeRateById(fromId: Int, toId: Int): CurrencyExchangeRate {
        return exchangeRateQueries.selectExchangeRateById(fromId.toLong(),toId.toLong()).executeAsOne().let {
            CurrencyExchangeRate(
                Currency(
                    id = it.fromCurrencyId.toInt(),
                    name = it.fromCurrencyName,
                    sign = it.fromCurrencySign
                ), Currency(
                    id = it.toCurrencyId.toInt(),
                    name = it.toCurrencyName,
                    sign = it.toCurrencySign
                ), it.rate ?: 1.0
            )
        }
    }

    override suspend fun getExchangeRatesOfCurrency(currency: Currency): Flow<List<CurrencyExchangeRate>> {
        return exchangeRateQueries.getExchangeRatesOfCurrency(currency.id.toLong()) { fromCurrencyId: Long, fromCurrencyName: String, fromCurrencySign: String, toCurrencyId: Long, toCurrencyName: String, toCurrencySign: String, rate: Double? ->
            CurrencyExchangeRate(
                Currency(
                    id = fromCurrencyId.toInt(),
                    name = fromCurrencyName,
                    sign = fromCurrencySign
                ), Currency(
                    id = toCurrencyId.toInt(),
                    name = toCurrencyName,
                    sign = toCurrencySign
                ), rate ?: 1.0
            )
        }.asFlow().mapToList(Dispatchers.IO)
    }
}