package data.local

import com.lightfeather.core.data.datasource.CurrencyExchangeRateDatasource
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate
import com.lightfeather.masarify.database.CurrencyExchangeRateQueries

class CurrencyExchangeRateDatasourceLocalImp(private val exchangeRateQueries: CurrencyExchangeRateQueries) :
    CurrencyExchangeRateDatasource {
    override suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int {
        exchangeRateQueries.insertExchangeRate(rate.from.id.toLong(), rate.to.id.toLong(), rate.rate)
        return exchangeRateQueries.selectLastInsertedRowId().executeAsOne().toInt()
    }

    override suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>) {
        rates.forEach { rateList ->
            rateList.forEach { rate ->
                exchangeRateQueries.insertExchangeRate(rate.from.id.toLong(), rate.to.id.toLong(), rate.rate)
            }
        }
    }

    override suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean {
        exchangeRateQueries.deleteExchangeRateById(rate.from.id.toLong(), rate.to.id.toLong())
        return true
    }

    override suspend fun getAllCurrenciesExchangeRates(): List<List<CurrencyExchangeRate>> {
        val resRates = mutableListOf<MutableList<CurrencyExchangeRate>>()
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
        mappedValues.forEachIndexed { idx, i ->
            resRates[idx] += mappedValues.filter { j -> i.from.id == j.from.id }
        }
        return resRates
    }

    override suspend fun getCurrencyExchangeRateById(id: Int): CurrencyExchangeRate {
        return exchangeRateQueries.selectExchangeRateById(id.toLong()).executeAsOne().let {
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

    override suspend fun getExchangeRatesOfCurrency(currency: Currency): List<CurrencyExchangeRate> {
        return exchangeRateQueries.getExchangeRatesOfCurrency(currency.id.toLong()).executeAsList().map {
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
}