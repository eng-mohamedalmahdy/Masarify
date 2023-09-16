package com.lightfeather.core.domain

data class Category(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String,
    val icon: String
)
