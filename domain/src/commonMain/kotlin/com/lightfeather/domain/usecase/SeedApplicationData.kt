package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.error.AppError
import com.lightfeather.domain.repository.UserRepository
import io.github.aakira.napier.Napier

/**
 * Coordinator use case for seeding all application default data on first launch.
 *
 * This use case orchestrates the seeding of:
 * - Default categories (income, expense categories)
 * - Default currencies (traditional, metals, crypto, stocks)
 * - Default bank names (Egyptian, Gulf, international banks)
 *
 * The operation is safe and idempotent:
 * - Checks if data is already seeded before attempting
 * - Marks as seeded only if ALL operations succeed
 * - Never throws exceptions (comprehensive error handling)
 * - Runs only once per installation
 *
 * @param userRepository Repository for tracking seeding status
 * @param seedDefaultCategories Use case for seeding categories
 * @param seedDefaultCurrencies Use case for seeding currencies
 * @param seedDefaultBankNames Use case for seeding bank names
 */
class SeedApplicationData(
    private val userRepository: UserRepository,
    private val seedDefaultCategories: SeedDefaultCategories,
    private val seedDefaultCurrencies: SeedDefaultCurrencies,
    private val seedDefaultBankNames: SeedDefaultBankNames,
) {
    /**
     * Seeds all application data if not already seeded.
     *
     * @return DomainResult indicating success or failure of the seeding operation
     */
    @Suppress("TooGenericExceptionCaught", "ReturnCount")
    suspend operator fun invoke(): DomainResult<Boolean> {
        return try {
            // Check if already seeded to avoid duplicate data
            if (userRepository.isDataSeeded()) {
                Napier.d { "Application data already seeded, skipping..." }
                return DomainResult.Success(true)
            }

            Napier.d { "Starting application data seeding..." }

            // Seed categories (15 default categories)
            Napier.d { "Seeding default categories..." }
            val categoriesResult = seedDefaultCategories()
            if (categoriesResult is DomainResult.Failure) {
                Napier.e { "Failed to seed categories: $categoriesResult" }
                return categoriesResult.map { false }
            }

            // Seed currencies (62 currencies: traditional, metals, crypto, stocks)
            Napier.d { "Seeding default currencies..." }
            val currenciesResult = seedDefaultCurrencies()
            if (currenciesResult is DomainResult.Failure) {
                Napier.e { "Failed to seed currencies: $currenciesResult" }
                return currenciesResult.map { false }
            }

            // Seed bank names (26 banks: Egyptian, Gulf, international)
            Napier.d { "Seeding default bank names..." }
            val bankNamesResult = seedDefaultBankNames()
            if (bankNamesResult is DomainResult.Failure) {
                return bankNamesResult.map { false }
            }

            // Mark as seeded only if all operations succeeded
            userRepository.markDataAsSeeded()
            Napier.d { "Application data seeding completed successfully!" }

            DomainResult.Success(true)
        } catch (e: Exception) {
            // Catch any unexpected exceptions to prevent app crashes
            Napier.e("Application data seeding failed with exception: ${e.message}", e)
            DomainResult.Failure(AppError.InternalError("Seeding failed: ${e.message ?: "Unknown error"}"))
        }
    }
}
