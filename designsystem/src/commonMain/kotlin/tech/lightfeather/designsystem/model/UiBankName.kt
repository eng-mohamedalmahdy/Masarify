package tech.lightfeather.designsystem.model

/**
 * UI representation of a bank name in the financial system.
 *
 * Bank names can be pre-seeded defaults or custom user-defined entries.
 * Used as dropdown options when creating bank accounts.
 */
data class UiBankName(
    val id: String,
    val name: String,
    val resourceKey: String? = null,
    val logoUrl: String? = null,
    val isDefault: Boolean = false,
) {
    companion object {
        val dummy =
            UiBankName(
                id = "1",
                name = "National Bank of Egypt",
                resourceKey = "bank_nbe",
                logoUrl = null,
                isDefault = true,
            )

        val empty =
            UiBankName(
                id = "",
                name = "",
                resourceKey = null,
                logoUrl = null,
                isDefault = false,
            )
    }
}
