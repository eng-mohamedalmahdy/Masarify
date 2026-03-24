package com.lightfeather.designsystem.model

import androidx.compose.runtime.Composable
import com.lightfeather.designsystem.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

/**
 * Get the localized name for a bank
 * If the bank is a default bank with a resource key, returns the localized string
 * Otherwise, returns the bank name as-is
 */
@Composable
fun UiBankName.getLocalizedName(): String =
    if (isDefault && resourceKey != null) {
        val stringRes = getBankStringResourceByKey(resourceKey!!)
        stringResource(stringRes)
    } else {
        name
    }

@Composable
fun String.getBankLocalizedName(): String {
    val bankName = getBankStringResourceByKey(this)
    if (bankName != MR.strings.bank_custom) {
        return stringResource(bankName)
    }
    return this
}

/**
 * Map resource key to MR.strings StringResource
 * This uses a when statement to map keys to actual string resources for all 26 banks
 */
@Suppress("CyclomaticComplexMethod") // Large when statement for resource mapping is acceptable
fun getBankStringResourceByKey(key: String): StringResource =
    when (key) {
        // ============================
        // EGYPTIAN BANKS
        // ============================

        "bank_nbe" -> MR.strings.bank_nbe
        "bank_banque_misr" -> MR.strings.bank_banque_misr
        "bank_cib" -> MR.strings.bank_cib
        "bank_qnb_alahli" -> MR.strings.bank_qnb_alahli
        "bank_alex_bank" -> MR.strings.bank_alex_bank
        "bank_banque_du_caire" -> MR.strings.bank_banque_du_caire
        "bank_saib" -> MR.strings.bank_saib
        "bank_aaib" -> MR.strings.bank_aaib
        "bank_arab_african" -> MR.strings.bank_arab_african
        "bank_egyptian_gulf" -> MR.strings.bank_egyptian_gulf
        "bank_credit_agricole_egypt" -> MR.strings.bank_credit_agricole_egypt
        "bank_faisal_islamic" -> MR.strings.bank_faisal_islamic

        // ============================
        // GULF COUNTRIES BANKS
        // ============================

        "bank_emirates_nbd" -> MR.strings.bank_emirates_nbd
        "bank_al_rajhi" -> MR.strings.bank_al_rajhi
        "bank_qatar_national" -> MR.strings.bank_qatar_national
        "bank_kuwait_finance_house" -> MR.strings.bank_kuwait_finance_house
        "bank_nbk" -> MR.strings.bank_nbk
        "bank_dubai_islamic" -> MR.strings.bank_dubai_islamic
        "bank_arab_bank" -> MR.strings.bank_arab_bank
        "bank_riyad" -> MR.strings.bank_riyad

        // ============================
        // INTERNATIONAL BANKS
        // ============================

        "bank_hsbc" -> MR.strings.bank_hsbc
        "bank_citibank" -> MR.strings.bank_citibank
        "bank_barclays" -> MR.strings.bank_barclays
        "bank_standard_chartered" -> MR.strings.bank_standard_chartered
        "bank_deutsche_bank" -> MR.strings.bank_deutsche_bank

        // ============================
        // SPECIAL OPTIONS
        // ============================

        "bank_cash" -> MR.strings.bank_cash
        "bank_custom" -> MR.strings.bank_custom

        // Fallback - return custom as default
        else -> MR.strings.bank_custom
    }
