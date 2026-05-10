package tech.lightfeather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppLanguage(
    val code: String,
    val languageName: String,
    val isRtl: Boolean,
)

object AppLanguages {
    val English = AppLanguage("en", "English", false)
    val Arabic = AppLanguage("ar", "العربية", true)

    fun fromCode(code: String): AppLanguage =
        when (code) {
            English.code -> English
            Arabic.code -> Arabic
            else -> English // Default to English
        }
}
