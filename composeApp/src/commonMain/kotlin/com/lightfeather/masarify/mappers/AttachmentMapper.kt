package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.AttachmentEntityType

fun Attachment.toUiAttachment(): UiAttachment =
    UiAttachment(
        id = id.toString(),
        name = fileName,
        mimeType = mimeType,
        fileContent = fileContent,
    )

fun UiAttachment.toAttachment(
    entityType: AttachmentEntityType,
    entityId: Int,
): Attachment =
    Attachment(
        id = id.toIntOrNull() ?: -1,
        entityType = entityType,
        entityId = entityId,
        mimeType = mimeType,
        fileName = name,
        fileContent = fileContent,
    )

/**
 * Convenience function for backward compatibility with transaction attachments
 */
fun UiAttachment.toTransactionAttachment(transactionId: Int): Attachment =
    toAttachment(AttachmentEntityType.TRANSACTION, transactionId)
