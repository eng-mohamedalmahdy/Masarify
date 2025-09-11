package com.lightfeather.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class AppLanguage(
    val code: String,
    val languageName: String,
    val isRtl: Boolean,
) {
    @Serializable
    object English : AppLanguage("en", "English", false)

    @Serializable
    object Arabic : AppLanguage("ar", "العربية", true)

    companion object {
        fun fromCode(code: String): AppLanguage =
            when (code) {
                English.code -> English
                Arabic.code -> Arabic
                else -> English // Default to English
            }
    }
}
