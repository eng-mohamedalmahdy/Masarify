package com.lightfeather.domain.model

/**
 * Defines all default currencies, metals, cryptocurrencies, and stocks seeded into the application.
 *
 * Each entry includes:
 * - resourceKey: Translation key for localized names/descriptions
 * - sign: Currency symbol, ticker, or standard code
 * - type: Classification (TRADITIONAL, METAL, CRYPTO, STOCK)
 *
 * Resource keys follow the pattern: currency_{key}, currency_{key}_desc
 */
@Suppress("MagicNumber")
enum class DefaultCurrency(
    val resourceKey: String,
    val sign: String,
    val type: CurrencyType,
) {
    // ============================
    // TRADITIONAL CURRENCIES (Fiat)
    // ============================

    // Major World Currencies
    USD("currency_usd", "$", CurrencyType.TRADITIONAL),
    EUR("currency_eur", "€", CurrencyType.TRADITIONAL),
    GBP("currency_gbp", "£", CurrencyType.TRADITIONAL),
    JPY("currency_jpy", "¥", CurrencyType.TRADITIONAL),
    CNY("currency_cny", "¥", CurrencyType.TRADITIONAL),
    CHF("currency_chf", "CHF", CurrencyType.TRADITIONAL),
    CAD("currency_cad", "C$", CurrencyType.TRADITIONAL),
    AUD("currency_aud", "A$", CurrencyType.TRADITIONAL),

    // Middle East & North Africa
    EGP("currency_egp", "E£", CurrencyType.TRADITIONAL),
    SAR("currency_sar", "﷼", CurrencyType.TRADITIONAL),
    AED("currency_aed", "د.إ", CurrencyType.TRADITIONAL),
    KWD("currency_kwd", "د.ك", CurrencyType.TRADITIONAL),
    QAR("currency_qar", "ر.ق", CurrencyType.TRADITIONAL),
    BHD("currency_bhd", "د.ب", CurrencyType.TRADITIONAL),
    OMR("currency_omr", "ر.ع", CurrencyType.TRADITIONAL),
    JOD("currency_jod", "د.ا", CurrencyType.TRADITIONAL),
    LBP("currency_lbp", "ل.ل", CurrencyType.TRADITIONAL),

    // Other Notable Currencies
    TRY("currency_try", "₺", CurrencyType.TRADITIONAL),
    INR("currency_inr", "₹", CurrencyType.TRADITIONAL),
    RUB("currency_rub", "₽", CurrencyType.TRADITIONAL),

    // ============================
    // PRECIOUS METALS & COMMODITIES
    // ============================

    GOLD("currency_gold", "XAU", CurrencyType.METAL),
    SILVER("currency_silver", "XAG", CurrencyType.METAL),
    PLATINUM("currency_platinum", "XPT", CurrencyType.METAL),
    PALLADIUM("currency_palladium", "XPD", CurrencyType.METAL),

    // ============================
    // CRYPTOCURRENCIES
    // ============================

    BITCOIN("currency_bitcoin", "BTC", CurrencyType.CRYPTO),
    ETHEREUM("currency_ethereum", "ETH", CurrencyType.CRYPTO),
    TETHER("currency_tether", "USDT", CurrencyType.CRYPTO),
    BINANCE_COIN("currency_binance_coin", "BNB", CurrencyType.CRYPTO),
    XRP("currency_xrp", "XRP", CurrencyType.CRYPTO),
    CARDANO("currency_cardano", "ADA", CurrencyType.CRYPTO),
    SOLANA("currency_solana", "SOL", CurrencyType.CRYPTO),
    DOGECOIN("currency_dogecoin", "DOGE", CurrencyType.CRYPTO),
    POLKADOT("currency_polkadot", "DOT", CurrencyType.CRYPTO),
    POLYGON("currency_polygon", "MATIC", CurrencyType.CRYPTO),
    LITECOIN("currency_litecoin", "LTC", CurrencyType.CRYPTO),
    AVALANCHE("currency_avalanche", "AVAX", CurrencyType.CRYPTO),

    // ============================
    // STOCKS (Major Companies)
    // ============================

    APPLE("currency_apple", "AAPL", CurrencyType.STOCK),
    GOOGLE("currency_google", "GOOGL", CurrencyType.STOCK),
    MICROSOFT("currency_microsoft", "MSFT", CurrencyType.STOCK),
    AMAZON("currency_amazon", "AMZN", CurrencyType.STOCK),
    TESLA("currency_tesla", "TSLA", CurrencyType.STOCK),
    META("currency_meta", "META", CurrencyType.STOCK),
    NVIDIA("currency_nvidia", "NVDA", CurrencyType.STOCK),
    ALIBABA("currency_alibaba", "BABA", CurrencyType.STOCK),
    DISNEY("currency_disney", "DIS", CurrencyType.STOCK),
    NETFLIX("currency_netflix", "NFLX", CurrencyType.STOCK),
    INTEL("currency_intel", "INTC", CurrencyType.STOCK),
    AMD("currency_amd", "AMD", CurrencyType.STOCK),
    ;

    /**
     * Converts this default currency enum to a domain Currency model.
     *
     * @return Currency model with resourceKey for localization support
     */
    fun toCurrency(): Currency =
        Currency(
            name = resourceKey,
            sign = sign,
            type = type,
            isDefault = false,
            resourceKey = resourceKey,
        )

    companion object {
        /**
         * Returns all default currencies across all types.
         */
        fun getAllCurrencies(): List<Currency> = entries.map { it.toCurrency() }

        /**
         * Returns all currencies of a specific type.
         *
         * @param type The currency type to filter by
         * @return List of currencies matching the specified type
         */
        fun getCurrenciesByType(type: CurrencyType): List<Currency> =
            entries.filter { it.type == type }.map { it.toCurrency() }

        /**
         * Returns all traditional fiat currencies.
         */
        fun getTraditionalCurrencies(): List<Currency> = getCurrenciesByType(CurrencyType.TRADITIONAL)

        /**
         * Returns all precious metals.
         */
        fun getMetals(): List<Currency> = getCurrenciesByType(CurrencyType.METAL)

        /**
         * Returns all cryptocurrencies.
         */
        fun getCryptocurrencies(): List<Currency> = getCurrenciesByType(CurrencyType.CRYPTO)

        /**
         * Returns all stock market securities.
         */
        fun getStocks(): List<Currency> = getCurrenciesByType(CurrencyType.STOCK)
    }
}
