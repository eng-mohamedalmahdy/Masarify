package tech.lightfeather.masarify.mappers

import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.designsystem.model.UiCurrencyExchangeRate
import tech.lightfeather.designsystem.model.UiCurrencyType
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.CurrencyExchangeRate
import tech.lightfeather.domain.model.CurrencyType

fun Currency.toUiCurrency(): UiCurrency =
    UiCurrency(
        id = id.toString(),
        name = name,
        symbol = sign,
        type = type.toUiCurrencyType(),
        isDefault = isDefault,
        resourceKey = resourceKey,
        isoCode = isoCode,
    )

fun UiCurrency.toCurrency(): Currency =
    Currency(
        id = id.toIntOrNull() ?: -1,
        name = name,
        sign = symbol,
        type = type.toCurrencyType(),
        isDefault = isDefault,
        resourceKey = resourceKey,
        isoCode = isoCode,
    )

fun CurrencyType.toUiCurrencyType(): UiCurrencyType =
    when (this) {
        CurrencyType.TRADITIONAL -> UiCurrencyType.TRADITIONAL
        CurrencyType.METAL -> UiCurrencyType.METAL
        CurrencyType.CRYPTO -> UiCurrencyType.CRYPTO
        CurrencyType.STOCK -> UiCurrencyType.STOCK
    }

fun UiCurrencyType.toCurrencyType(): CurrencyType =
    when (this) {
        UiCurrencyType.TRADITIONAL -> CurrencyType.TRADITIONAL
        UiCurrencyType.METAL -> CurrencyType.METAL
        UiCurrencyType.CRYPTO -> CurrencyType.CRYPTO
        UiCurrencyType.STOCK -> CurrencyType.STOCK
    }

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
