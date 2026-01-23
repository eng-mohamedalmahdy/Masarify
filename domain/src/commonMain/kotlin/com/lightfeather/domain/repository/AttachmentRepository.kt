package com.lightfeather.domain.repository

import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.DomainResult

/**
 * Repository interface for attachment operations
 * Handles CRUD operations for transaction attachments
 */
interface AttachmentRepository {
    /**
     * Create a new attachment
     *
     * @param attachment Attachment to create
     * @return DomainResult containing the created attachment ID
     */
    suspend fun createAttachment(attachment: Attachment): DomainResult<Int>

    /**
     * Delete an attachment by ID
     *
     * @param id Attachment ID
     * @return DomainResult indicating success/failure
     */
    suspend fun deleteAttachment(id: Int): DomainResult<Boolean>

    /**
     * Get all attachments for a transaction
     *
     * @param transactionId Transaction ID
     * @return DomainResult containing list of attachments
     */
    suspend fun getAttachmentsByTransactionId(transactionId: Int): DomainResult<List<Attachment>>

    /**
     * Delete all attachments for a transaction
     * Useful when deleting a transaction
     *
     * @param transactionId Transaction ID
     * @return DomainResult indicating success/failure
     */
    suspend fun deleteAttachmentsByTransactionId(transactionId: Int): DomainResult<Boolean>
}
