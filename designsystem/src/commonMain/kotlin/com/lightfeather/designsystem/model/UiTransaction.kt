package com.lightfeather.designsystem.model

import com.lightfeather.designsystem.util.now
import kotlinx.datetime.LocalDateTime

data class UiTransaction(
    val id: String,
    val type: UiTransactionType,
    val amount: String,
    val dateTime: LocalDateTime,
    val description: String,
    val category: UiCategory,
    val hasAttachment: Boolean,
) {
    companion object {
        val dummy =
            UiTransaction(
                id = "1",
                amount = "100",
                type = UiTransactionType.EXPENSE,
                dateTime = LocalDateTime.now(),
                description = "Coffee Shop Purchase",
                category = UiCategory.dummy,
                hasAttachment = false,
            )
    }
}
