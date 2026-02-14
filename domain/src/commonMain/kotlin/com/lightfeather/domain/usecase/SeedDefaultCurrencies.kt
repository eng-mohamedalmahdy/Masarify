package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DefaultCurrency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.CurrencyRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first

/**
 * Use case for seeding default currencies into the database on first launch.
 *
 * Seeds all currencies defined in [DefaultCurrency] enum including:
 * - Traditional fiat currencies (USD, EUR, EGP, etc.)
 * - Precious metals (Gold, Silver, Platinum, Palladium)
 * - Cryptocurrencies (Bitcoin, Ethereum, etc.)
 * - Stock market securities (AAPL, GOOGL, etc.)
 *
 * This operation is idempotent - if currencies already exist, seeding is skipped.
 */
class SeedDefaultCurrencies(
    private val currencyRepository: CurrencyRepository,
) {
    /**
     * Seeds all default currencies into the database.
     *
     * @return DomainResult indicating success or failure of the seeding operation
     */
    suspend operator fun invoke(): DomainResult<Boolean> =
        currencyRepository.getAllCurrencies().flatMapSuspend { currenciesFlow ->
            // Collect current currencies to check if seeding needed
            val existingCurrencies: List<Currency> = currenciesFlow.first()

            // Skip seeding if currencies already exist
            if (existingCurrencies.isNotEmpty()) {
                Napier.d { "Currencies already seeded, skipping..." }
                return@flatMapSuspend DomainResult.Success(true)
            }

            // Seed all default currencies
            Napier.d { "Seeding ${DefaultCurrency.entries.size} default currencies..." }
            val defaultCurrencies = DefaultCurrency.getAllCurrencies()

            for (currency in defaultCurrencies) {
                val result = currencyRepository.createCurrency(currency)
                if (result is DomainResult.Failure) {
                    Napier.e { "Failed to seed currency: ${currency.name}, error: $result" }
                    return@flatMapSuspend result.map { false }
                }
            }

            Napier.d { "Successfully seeded ${defaultCurrencies.size} currencies" }
            DomainResult.Success(true)
        }
}
