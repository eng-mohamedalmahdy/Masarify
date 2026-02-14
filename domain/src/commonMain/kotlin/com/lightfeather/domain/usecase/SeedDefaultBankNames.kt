package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.DefaultBankName
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.BankNameRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first

/**
 * Use case for seeding default bank names into the database on first launch.
 *
 * Seeds all bank names defined in [DefaultBankName] enum including:
 * - Egyptian banks (NBE, Banque Misr, CIB, etc.)
 * - Gulf region banks (Emirates NBD, Al Rajhi, QNB, etc.)
 * - International banks (HSBC, Citibank, Barclays, etc.)
 *
 * This operation is idempotent - if bank names already exist, seeding is skipped.
 */
class SeedDefaultBankNames(
    private val bankNameRepository: BankNameRepository,
) {
    /**
     * Seeds all default bank names into the database.
     *
     * @return DomainResult indicating success or failure of the seeding operation
     */
    suspend operator fun invoke(): DomainResult<Boolean> =
        bankNameRepository.getAllBankNames().flatMapSuspend { bankNamesFlow ->
            // Collect current bank names to check if seeding needed
            val existingBankNames = bankNamesFlow.first()

            // Skip seeding if bank names already exist
            if (existingBankNames.isNotEmpty()) {
                Napier.d { "Bank names already seeded, skipping..." }
                return@flatMapSuspend DomainResult.Success(true)
            }

            // Seed all default bank names
            Napier.d { "Seeding ${DefaultBankName.getAllBankNames().size} default bank names..." }
            val defaultBankNames = DefaultBankName.getAllBankNames()

            for (bankName in defaultBankNames) {
                val result = bankNameRepository.createBankName(bankName)
                if (result is DomainResult.Failure) {
                    Napier.e { "Failed to seed bank name: ${bankName.name}, error: $result" }
                    return@flatMapSuspend result.map { false }
                }
            }

            Napier.d { "Successfully seeded ${defaultBankNames.size} bank names" }
            DomainResult.Success(true)
        }
}
