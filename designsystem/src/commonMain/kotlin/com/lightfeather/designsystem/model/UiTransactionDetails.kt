package com.lightfeather.designsystem.model

data class UiTransactionDetails(
    val id: String,
    val amount: String,
    val description: String,
    val date: String,
    val time: String,
    val categories: List<UiCategory>,
    val attachments: List<UiAttachment>,
)
