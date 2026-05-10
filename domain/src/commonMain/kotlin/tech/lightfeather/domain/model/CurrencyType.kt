package tech.lightfeather.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the type/category of a currency in the financial system.
 *
 * Used to classify currencies for filtering and display purposes in the UI.
 */
@Serializable
enum class CurrencyType {
    /**
     * Traditional fiat currencies issued by governments (USD, EUR, EGP, etc.)
     */
    TRADITIONAL,

    /**
     * Precious metals and commodities (Gold, Silver, Platinum, etc.)
     */
    METAL,

    /**
     * Cryptocurrencies and digital assets (Bitcoin, Ethereum, etc.)
     */
    CRYPTO,

    /**
     * Stock market securities and shares (AAPL, GOOGL, TSLA, etc.)
     */
    STOCK,
}
