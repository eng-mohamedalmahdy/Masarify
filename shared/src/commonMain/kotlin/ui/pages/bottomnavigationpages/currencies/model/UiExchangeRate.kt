package ui.pages.bottomnavigationpages.currencies.model

import com.lightfeather.core.domain.CurrencyExchangeRate
import ui.entity.UiCurrency
import ui.entity.toDomainCurrency
import ui.entity.toUiCurrency

data class UiExchangeRate(
    val fromCurrency: UiCurrency,
    val toCurrency: UiCurrency,
    val rate: Double
)

fun CurrencyExchangeRate.toUiExchangeRate() = UiExchangeRate(from.toUiCurrency(), to.toUiCurrency(), rate)
fun UiExchangeRate.toDomainExchangeRate() =
    CurrencyExchangeRate(fromCurrency.toDomainCurrency(), toCurrency.toDomainCurrency(), rate)