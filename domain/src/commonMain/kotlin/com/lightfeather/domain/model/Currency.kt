package com.lightfeather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val name: String,
    val sign: String,
    val id: Int = -1,
    val type: CurrencyType = CurrencyType.TRADITIONAL,
    val isDefault: Boolean = false,
    val resourceKey: String? = null,
    val isoCode: String? = null,
)
