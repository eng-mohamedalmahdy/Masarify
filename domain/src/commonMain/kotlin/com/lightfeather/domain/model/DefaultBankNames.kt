package com.lightfeather.domain.model

/**
 * Defines all default bank names seeded into the application.
 *
 * Includes major banks from Egypt, Gulf countries, and international institutions.
 * Users can select these when creating accounts or add custom bank names.
 *
 * Each entry includes:
 * - resourceKey: Translation key for localized bank names
 * - logoUrl: Optional path/URL for bank logo (can be added later)
 *
 * Resource keys follow the pattern: bank_{key}
 */
enum class DefaultBankName(
    val resourceKey: String,
    val logoUrl: String? = null,
) {
    // ============================
    // EGYPTIAN BANKS
    // ============================

    NATIONAL_BANK_OF_EGYPT("bank_nbe"),
    BANQUE_MISR("bank_banque_misr"),
    COMMERCIAL_INTERNATIONAL_BANK("bank_cib"),
    QNB_ALAHLI("bank_qnb_alahli"),
    ALEX_BANK("bank_alex_bank"),
    BANQUE_DU_CAIRE("bank_banque_du_caire"),
    SAIB("bank_saib"),
    AAIB("bank_aaib"),
    ARAB_AFRICAN_INTERNATIONAL_BANK("bank_arab_african"),
    EGYPTIAN_GULF_BANK("bank_egyptian_gulf"),
    CREDIT_AGRICOLE_EGYPT("bank_credit_agricole_egypt"),
    FAISAL_ISLAMIC_BANK("bank_faisal_islamic"),

    // ============================
    // GULF COUNTRIES BANKS
    // ============================

    EMIRATES_NBD("bank_emirates_nbd"),
    AL_RAJHI_BANK("bank_al_rajhi"),
    QATAR_NATIONAL_BANK("bank_qatar_national"),
    KUWAIT_FINANCE_HOUSE("bank_kuwait_finance_house"),
    NATIONAL_BANK_OF_KUWAIT("bank_nbk"),
    DUBAI_ISLAMIC_BANK("bank_dubai_islamic"),
    ARAB_BANK("bank_arab_bank"),
    RIYAD_BANK("bank_riyad"),

    // ============================
    // INTERNATIONAL BANKS
    // ============================

    HSBC("bank_hsbc"),
    CITIBANK("bank_citibank"),
    BARCLAYS("bank_barclays"),
    STANDARD_CHARTERED("bank_standard_chartered"),
    DEUTSCHE_BANK("bank_deutsche_bank"),

    // ============================
    // SPECIAL OPTIONS
    // ============================

    /**
     * Represents a cash account (no bank).
     */
    CASH("bank_cash"),

    /**
     * Represents a custom bank name entered by the user (not a pre-seeded bank).
     */
    CUSTOM("bank_custom"),
    ;

    /**
     * Converts this default bank name enum to a domain BankName model.
     *
     * @return BankName model with resourceKey for localization support
     */
    fun toBankName(): BankName =
        BankName(
            name = resourceKey,
            resourceKey = resourceKey,
            logoUrl = logoUrl,
            isDefault = true,
        )

    companion object {
        /**
         * Returns all default bank names (excluding CUSTOM).
         */
        fun getAllBankNames(): List<BankName> = entries.filter { it != CUSTOM }.map { it.toBankName() }

        /**
         * Returns only Egyptian banks.
         */
        fun getEgyptianBanks(): List<BankName> =
            listOf(
                NATIONAL_BANK_OF_EGYPT,
                BANQUE_MISR,
                COMMERCIAL_INTERNATIONAL_BANK,
                QNB_ALAHLI,
                ALEX_BANK,
                BANQUE_DU_CAIRE,
                SAIB,
                AAIB,
                ARAB_AFRICAN_INTERNATIONAL_BANK,
                EGYPTIAN_GULF_BANK,
                CREDIT_AGRICOLE_EGYPT,
                FAISAL_ISLAMIC_BANK,
            ).map { it.toBankName() }

        /**
         * Returns only Gulf region banks.
         */
        fun getGulfBanks(): List<BankName> =
            listOf(
                EMIRATES_NBD,
                AL_RAJHI_BANK,
                QATAR_NATIONAL_BANK,
                KUWAIT_FINANCE_HOUSE,
                NATIONAL_BANK_OF_KUWAIT,
                DUBAI_ISLAMIC_BANK,
                ARAB_BANK,
                RIYAD_BANK,
            ).map { it.toBankName() }

        /**
         * Returns only international banks.
         */
        fun getInternationalBanks(): List<BankName> =
            listOf(
                HSBC,
                CITIBANK,
                BARCLAYS,
                STANDARD_CHARTERED,
                DEUTSCHE_BANK,
            ).map { it.toBankName() }
    }
}
