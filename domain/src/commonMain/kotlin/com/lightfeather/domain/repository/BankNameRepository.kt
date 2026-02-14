package com.lightfeather.domain.repository

import com.lightfeather.domain.model.BankName
import com.lightfeather.domain.model.DomainResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing bank names in the application.
 *
 * Provides CRUD operations for bank names, which can be pre-seeded defaults
 * or custom user-defined entries used when creating bank accounts.
 */
interface BankNameRepository {
    /**
     * Creates a new bank name in the database.
     *
     * @param bankName The bank name to create
     * @return DomainResult containing the ID of the created bank name, or an error
     */
    suspend fun createBankName(bankName: BankName): DomainResult<Int>

    /**
     * Updates an existing bank name.
     *
     * @param bankName The bank name with updated information
     * @return DomainResult indicating success or failure
     */
    suspend fun updateBankName(bankName: BankName): DomainResult<Boolean>

    /**
     * Deletes a bank name from the database.
     *
     * @param bankName The bank name to delete
     * @return DomainResult indicating success or failure
     */
    suspend fun deleteBankName(bankName: BankName): DomainResult<Boolean>

    /**
     * Retrieves a specific bank name by its ID.
     *
     * @param id The unique identifier of the bank name
     * @return DomainResult containing the bank name, or an error if not found
     */
    suspend fun getBankNameById(id: Int): DomainResult<BankName>

    /**
     * Retrieves all bank names as a reactive Flow.
     *
     * @return DomainResult containing a Flow of all bank names, or an error
     */
    fun getAllBankNames(): DomainResult<Flow<List<BankName>>>
}
