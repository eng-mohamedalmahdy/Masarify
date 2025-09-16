package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.domain.model.Currency

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