package com.lightfeather.designsystem.model

data class UiTransaction(
    val id: String,
    val amount: String,
    val date: String,
    val time: String,
    val description: String,
    val category: UiCategory,
    val hasAttachment: Boolean
){
    companion object{
        val dummy = UiTransaction(
            id = "1",
            amount = "100",
            date = "2021-01-01",
            time = "12:00",
            description = "description",
            category = UiCategory.dummy,
            hasAttachment = false
        )
    }
}
