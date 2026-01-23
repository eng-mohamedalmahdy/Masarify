package com.lightfeather.domain.repository

import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.AttachmentEntityType
import com.lightfeather.domain.model.DomainResult

/**
 * Repository interface for attachment operations
 * Handles CRUD operations for attachments across multiple entity types
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
     * Get all attachments for a specific entity
     *
     * @param entityType Type of entity (transaction, category, bank account, etc.)
     * @param entityId ID of the entity
     * @return DomainResult containing list of attachments
     */
    suspend fun getAttachmentsByEntity(
        entityType: AttachmentEntityType,
        entityId: Int,
    ): DomainResult<List<Attachment>>

    /**
     * Delete all attachments for a specific entity
     * Useful when deleting an entity
     *
     * @param entityType Type of entity (transaction, category, bank account, etc.)
     * @param entityId ID of the entity
     * @return DomainResult indicating success/failure
     */
    suspend fun deleteAttachmentsByEntity(
        entityType: AttachmentEntityType,
        entityId: Int,
    ): DomainResult<Boolean>

    /**
     * Get all attachments for a transaction (convenience method for backward compatibility)
     *
     * @param transactionId Transaction ID
     * @return DomainResult containing list of attachments
     */
    suspend fun getAttachmentsByTransactionId(transactionId: Int): DomainResult<List<Attachment>> =
        getAttachmentsByEntity(AttachmentEntityType.TRANSACTION, transactionId)

    /**
     * Delete all attachments for a transaction (convenience method for backward compatibility)
     *
     * @param transactionId Transaction ID
     * @return DomainResult indicating success/failure
     */
    suspend fun deleteAttachmentsByTransactionId(transactionId: Int): DomainResult<Boolean> =
        deleteAttachmentsByEntity(AttachmentEntityType.TRANSACTION, transactionId)
}
