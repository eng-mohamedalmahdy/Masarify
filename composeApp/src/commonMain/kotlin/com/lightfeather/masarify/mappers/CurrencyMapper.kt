package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiCurrencyExchangeRate
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.CurrencyExchangeRate

fun Currency.toUiCurrency(): UiCurrency =
    UiCurrency(
        id = id.toString(),
        name = name,
        symbol = sign,
    )

fun UiCurrency.toCurrency(): Currency =
    Currency(
        id = id.toIntOrNull() ?: -1,
        name = name,
        sign = symbol,
    )

fun CurrencyExchangeRate.toUiCurrencyExchangeRate(): UiCurrencyExchangeRate =
    UiCurrencyExchangeRate(
        id = "${from.id}_${to.id}",
        fromCurrency = from.toUiCurrency(),
        toCurrency = to.toUiCurrency(),
        rate = rate,
    )

fun UiCurrencyExchangeRate.toCurrencyExchangeRate(): CurrencyExchangeRate =
    CurrencyExchangeRate(
        from = fromCurrency.toCurrency(),
        to = toCurrency.toCurrency(),
        rate = rate,
    )
