package com.lightfeather.designsystem.model

import kotlinx.datetime.LocalDateTime

data class UiTransactionDetails(
    val id: String,
    val type: UiTransactionType,
    val amount: String,
    val description: String,
    val dateTime: LocalDateTime,
    val categories: List<UiCategory>,
    val attachments: List<UiAttachment>,
)
