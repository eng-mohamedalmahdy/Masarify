package com.lightfeather.designsystem.model

data class UiCategory(
    val id: String,
    val name: String,
    val image: Any
){
    companion object{
        val dummy = UiCategory(
            id = "1",
            name = "Category 1",
            image = ""
        )
    }
}
