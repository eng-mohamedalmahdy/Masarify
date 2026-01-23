package com.lightfeather.domain.model

/**
 * Enum defining the types of entities that can have attachments
 * This enables a generic attachment system that can be used by multiple entities
 */
enum class AttachmentEntityType(
    val value: String,
) {
    /**
     * Attachment belongs to a transaction (receipts, invoices, etc.)
     */
    TRANSACTION("transaction"),

    /**
     * Attachment belongs to a category (category icons, images, etc.)
     */
    CATEGORY("category"),

    /**
     * Attachment belongs to a bank account (bank statements, logos, etc.)
     */
    BANK_ACCOUNT("bank_account"),
    ;

    companion object {
        /**
         * Get AttachmentEntityType from string value
         * @param value String representation of the entity type
         * @return AttachmentEntityType or null if not found
         */
        fun fromValue(value: String): AttachmentEntityType? = entries.find { it.value == value }
    }
}
