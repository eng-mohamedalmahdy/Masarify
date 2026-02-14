package com.lightfeather.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a bank name in the financial system.
 *
 * Bank names can be pre-seeded defaults or custom user-defined entries.
 * Used as dropdown options when creating bank accounts.
 *
 * @property id Unique identifier for the bank name
 * @property name Display name of the bank (stores resourceKey for defaults)
 * @property resourceKey Translation key for localization (null for custom entries)
 * @property logoUrl Optional URL or resource path for bank logo
 * @property isDefault Whether this is a pre-seeded bank (protected from deletion)
 */
@Serializable
data class BankName(
    val id: Int = -1,
    val name: String,
    val resourceKey: String? = null,
    val logoUrl: String? = null,
    val isDefault: Boolean = false,
)
