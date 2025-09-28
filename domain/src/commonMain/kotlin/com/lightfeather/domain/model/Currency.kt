package com.lightfeather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val name: String,
    val sign: String,
    val id: Int = -1,
)
