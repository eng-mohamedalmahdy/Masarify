package com.lightfeather.data.local

import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.UserData
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set

class AppPreferences(
    private val settings: Settings,
) {
    companion object {
        const val USER_DATA = "userData"
        const val DARK_MODE = "darkMode"
        const val DYNAMIC_COLORS = "dynamicColors"
        const val APP_LANGUAGE = "appLanguage"

        const val USER_SAVED_COLORS = "userSavedColors"
        const val DATA_SEEDED = "dataSeeded"
        const val BIOMETRIC_ENABLED = "biometricEnabled"
        const val BIOMETRIC_SUGGESTION_SHOWN = "biometricSuggestionShown"
    }

    var userData: UserData?
        set(value) = settings.encodeValue(USER_DATA, value)
        get() = settings.decodeValueOrNull<UserData>(USER_DATA)

    var isDarkMode: Boolean
        set(value) = settings.set(DARK_MODE, value)
        get() = settings[DARK_MODE] ?: false

    var isDynamicColors: Boolean
        set(value) = settings.set(DYNAMIC_COLORS, value)
        get() = settings[DYNAMIC_COLORS] ?: false

    var appLanguage: AppLanguage?
        set(value) = settings.encodeValue(APP_LANGUAGE, value)
        get() = settings.decodeValueOrNull<AppLanguage>(APP_LANGUAGE)

    var userSavedColors: List<String>
        set(value) = settings.encodeValue(USER_SAVED_COLORS, value)
        get() = settings.decodeValueOrNull<List<String>>(USER_SAVED_COLORS) ?: emptyList()

    var isDataSeeded: Boolean
        set(value) = settings.set(DATA_SEEDED, value)
        get() = settings[DATA_SEEDED] ?: false

    var isBiometricEnabled: Boolean
        set(value) = settings.set(BIOMETRIC_ENABLED, value)
        get() = settings[BIOMETRIC_ENABLED] ?: false

    var hasShownBiometricSuggestion: Boolean
        set(value) = settings.set(BIOMETRIC_SUGGESTION_SHOWN, value)
        get() = settings[BIOMETRIC_SUGGESTION_SHOWN] ?: false
}
