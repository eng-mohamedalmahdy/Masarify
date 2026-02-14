package com.lightfeather.designsystem.model

/**
 * Types of currencies supported in the financial system
 */
enum class UiCurrencyType {
    /** Traditional fiat currencies (USD, EUR, EGP, etc.) */
    TRADITIONAL,

    /** Precious metals (Gold, Silver, Platinum, Palladium) */
    METAL,

    /** Cryptocurrencies (Bitcoin, Ethereum, etc.) */
    CRYPTO,

    /** Stock market securities (AAPL, GOOGL, etc.) */
    STOCK,
}
