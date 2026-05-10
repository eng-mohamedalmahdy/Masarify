package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.BankName
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.BankNameRepository

/**
 * Use cases for managing bank names in the application.
 *
 * Provides CRUD operations for bank names which can be selected when creating accounts.
 */
class BankNameUseCase(
    private val bankNameRepository: BankNameRepository,
) {
    /**
     * Creates a new custom bank name.
     */
    class CreateBankName(
        private val bankNameRepository: BankNameRepository,
    ) {
        suspend operator fun invoke(bankName: BankName): DomainResult<Int> = bankNameRepository.createBankName(bankName)
    }

    /**
     * Updates an existing bank name.
     */
    class UpdateBankName(
        private val bankNameRepository: BankNameRepository,
    ) {
        suspend operator fun invoke(bankName: BankName): DomainResult<Boolean> =
            bankNameRepository.updateBankName(bankName)
    }

    /**
     * Deletes a bank name.
     */
    class DeleteBankName(
        private val bankNameRepository: BankNameRepository,
    ) {
        suspend operator fun invoke(bankName: BankName): DomainResult<Boolean> =
            bankNameRepository.deleteBankName(bankName)
    }

    /**
     * Retrieves a specific bank name by ID.
     */
    class GetBankNameById(
        private val bankNameRepository: BankNameRepository,
    ) {
        suspend operator fun invoke(id: Int): DomainResult<BankName> = bankNameRepository.getBankNameById(id)
    }

    /**
     * Retrieves all bank names as a reactive Flow.
     */
    class GetAllBankNames(
        private val bankNameRepository: BankNameRepository,
    ) {
        operator fun invoke(): DomainResult<Flow<List<BankName>>> = bankNameRepository.getAllBankNames()
    }
}
