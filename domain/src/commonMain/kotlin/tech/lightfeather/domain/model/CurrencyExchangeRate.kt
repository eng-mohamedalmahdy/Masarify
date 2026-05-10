package tech.lightfeather.domain.model

data class CurrencyExchangeRate(
    val from: Currency,
    val to: Currency,
    val rate: Double,
)
