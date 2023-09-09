package com.lightfeather.core.data.datasource

import com.lightfeather.core.domain.Currency

interface CurrencyDatasource {
  suspend  fun createCurrency(currency: Currency): Int
  suspend  fun updateCurrency(currency: Currency)
  suspend  fun deleteCurrency(currency: Currency): Boolean
  suspend  fun getAllCurrencies(): List<Currency>
  suspend  fun getCurrencyById(id: Int): Currency

}