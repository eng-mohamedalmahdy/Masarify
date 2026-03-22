package com.lightfeather.designsystem.model

import androidx.compose.runtime.Composable
import com.lightfeather.designsystem.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

/**
 * Get the localized name for a currency
 * If the currency is a default currency with a resource key, returns the localized string
 * Otherwise, returns the currency name as-is
 */
@Composable
fun UiCurrency.getLocalizedName(): String =
    if (resourceKey != null) {
        val stringRes = getCurrencyStringResourceByKey(resourceKey)
        stringResource(stringRes)
    } else {
        name
    }

/**
 * Get the localized description for a currency
 * If the currency is a default currency with a resource key, returns the localized description
 * Otherwise, returns null
 */
@Composable
fun UiCurrency.getLocalizedDescription(): String? =
    if (isDefault && resourceKey != null) {
        val descKey = "${resourceKey}_desc"
        val stringRes = getStringResourceByKey(descKey)
        stringResource(stringRes)
    } else {
        null
    }

/**
 * Map resource key to MR.strings StringResource
 * This uses a when statement to map keys to actual string resources for all 62 currencies
 */
@Suppress("CyclomaticComplexMethod", "LongMethod") // Large when statement for resource mapping
private fun getCurrencyStringResourceByKey(key: String): StringResource =
    when (key) {
        // ============================
        // TRADITIONAL CURRENCIES
        // ============================

        // Major World Currencies
        "currency_usd" -> MR.strings.currency_usd
        "currency_usd_desc" -> MR.strings.currency_usd_desc
        "currency_eur" -> MR.strings.currency_eur
        "currency_eur_desc" -> MR.strings.currency_eur_desc
        "currency_gbp" -> MR.strings.currency_gbp
        "currency_gbp_desc" -> MR.strings.currency_gbp_desc
        "currency_jpy" -> MR.strings.currency_jpy
        "currency_jpy_desc" -> MR.strings.currency_jpy_desc
        "currency_cny" -> MR.strings.currency_cny
        "currency_cny_desc" -> MR.strings.currency_cny_desc
        "currency_chf" -> MR.strings.currency_chf
        "currency_chf_desc" -> MR.strings.currency_chf_desc
        "currency_cad" -> MR.strings.currency_cad
        "currency_cad_desc" -> MR.strings.currency_cad_desc
        "currency_aud" -> MR.strings.currency_aud
        "currency_aud_desc" -> MR.strings.currency_aud_desc

        // Middle East & North Africa
        "currency_egp" -> MR.strings.currency_egp
        "currency_egp_desc" -> MR.strings.currency_egp_desc
        "currency_sar" -> MR.strings.currency_sar
        "currency_sar_desc" -> MR.strings.currency_sar_desc
        "currency_aed" -> MR.strings.currency_aed
        "currency_aed_desc" -> MR.strings.currency_aed_desc
        "currency_kwd" -> MR.strings.currency_kwd
        "currency_kwd_desc" -> MR.strings.currency_kwd_desc
        "currency_qar" -> MR.strings.currency_qar
        "currency_qar_desc" -> MR.strings.currency_qar_desc
        "currency_bhd" -> MR.strings.currency_bhd
        "currency_bhd_desc" -> MR.strings.currency_bhd_desc
        "currency_omr" -> MR.strings.currency_omr
        "currency_omr_desc" -> MR.strings.currency_omr_desc
        "currency_jod" -> MR.strings.currency_jod
        "currency_jod_desc" -> MR.strings.currency_jod_desc
        "currency_lbp" -> MR.strings.currency_lbp
        "currency_lbp_desc" -> MR.strings.currency_lbp_desc

        // Other Notable Currencies
        "currency_try" -> MR.strings.currency_try
        "currency_try_desc" -> MR.strings.currency_try_desc
        "currency_inr" -> MR.strings.currency_inr
        "currency_inr_desc" -> MR.strings.currency_inr_desc
        "currency_rub" -> MR.strings.currency_rub
        "currency_rub_desc" -> MR.strings.currency_rub_desc

        // ============================
        // PRECIOUS METALS & COMMODITIES
        // ============================

        "currency_gold" -> MR.strings.currency_gold
        "currency_gold_desc" -> MR.strings.currency_gold_desc
        "currency_silver" -> MR.strings.currency_silver
        "currency_silver_desc" -> MR.strings.currency_silver_desc
        "currency_platinum" -> MR.strings.currency_platinum
        "currency_platinum_desc" -> MR.strings.currency_platinum_desc
        "currency_palladium" -> MR.strings.currency_palladium
        "currency_palladium_desc" -> MR.strings.currency_palladium_desc

        // ============================
        // CRYPTOCURRENCIES
        // ============================

        "currency_bitcoin" -> MR.strings.currency_bitcoin
        "currency_bitcoin_desc" -> MR.strings.currency_bitcoin_desc
        "currency_ethereum" -> MR.strings.currency_ethereum
        "currency_ethereum_desc" -> MR.strings.currency_ethereum_desc
        "currency_tether" -> MR.strings.currency_tether
        "currency_tether_desc" -> MR.strings.currency_tether_desc
        "currency_binance_coin" -> MR.strings.currency_binance_coin
        "currency_binance_coin_desc" -> MR.strings.currency_binance_coin_desc
        "currency_xrp" -> MR.strings.currency_xrp
        "currency_xrp_desc" -> MR.strings.currency_xrp_desc
        "currency_cardano" -> MR.strings.currency_cardano
        "currency_cardano_desc" -> MR.strings.currency_cardano_desc
        "currency_solana" -> MR.strings.currency_solana
        "currency_solana_desc" -> MR.strings.currency_solana_desc
        "currency_dogecoin" -> MR.strings.currency_dogecoin
        "currency_dogecoin_desc" -> MR.strings.currency_dogecoin_desc
        "currency_polkadot" -> MR.strings.currency_polkadot
        "currency_polkadot_desc" -> MR.strings.currency_polkadot_desc
        "currency_polygon" -> MR.strings.currency_polygon
        "currency_polygon_desc" -> MR.strings.currency_polygon_desc
        "currency_litecoin" -> MR.strings.currency_litecoin
        "currency_litecoin_desc" -> MR.strings.currency_litecoin_desc
        "currency_avalanche" -> MR.strings.currency_avalanche
        "currency_avalanche_desc" -> MR.strings.currency_avalanche_desc

        // ============================
        // STOCKS (Major Companies)
        // ============================

        "currency_apple" -> MR.strings.currency_apple
        "currency_apple_desc" -> MR.strings.currency_apple_desc
        "currency_google" -> MR.strings.currency_google
        "currency_google_desc" -> MR.strings.currency_google_desc
        "currency_microsoft" -> MR.strings.currency_microsoft
        "currency_microsoft_desc" -> MR.strings.currency_microsoft_desc
        "currency_amazon" -> MR.strings.currency_amazon
        "currency_amazon_desc" -> MR.strings.currency_amazon_desc
        "currency_tesla" -> MR.strings.currency_tesla
        "currency_tesla_desc" -> MR.strings.currency_tesla_desc
        "currency_meta" -> MR.strings.currency_meta
        "currency_meta_desc" -> MR.strings.currency_meta_desc
        "currency_nvidia" -> MR.strings.currency_nvidia
        "currency_nvidia_desc" -> MR.strings.currency_nvidia_desc
        "currency_alibaba" -> MR.strings.currency_alibaba
        "currency_alibaba_desc" -> MR.strings.currency_alibaba_desc
        "currency_disney" -> MR.strings.currency_disney
        "currency_disney_desc" -> MR.strings.currency_disney_desc
        "currency_netflix" -> MR.strings.currency_netflix
        "currency_netflix_desc" -> MR.strings.currency_netflix_desc
        "currency_intel" -> MR.strings.currency_intel
        "currency_intel_desc" -> MR.strings.currency_intel_desc
        "currency_amd" -> MR.strings.currency_amd
        "currency_amd_desc" -> MR.strings.currency_amd_desc

        // Fallback - return USD as default
        else -> MR.strings.currency_usd
    }
