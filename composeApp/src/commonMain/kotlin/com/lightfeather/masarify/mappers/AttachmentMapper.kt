package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.domain.model.Attachment

fun Attachment.toUiAttachment(): UiAttachment =
    UiAttachment(
        id = id.toString(),
        name = fileName,
        mimeType = mimeType,
        fileContent = fileContent,
    )

fun UiAttachment.toAttachment(transactionId: Int = -1): Attachment =
    Attachment(
        id = id.toIntOrNull() ?: -1,
        transactionId = transactionId,
        mimeType = mimeType,
        fileName = name,
        fileContent = fileContent,
    )