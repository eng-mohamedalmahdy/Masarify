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
        const val AUTO_SYNC_RATES = "autoSyncRates"
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val REMOTE_USER_ID = "remoteUserId"
        const val LAST_SYNC_AT = "lastSyncAt"
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

    var isAutoSyncRatesEnabled: Boolean
        set(value) = settings.set(AUTO_SYNC_RATES, value)
        get() = settings[AUTO_SYNC_RATES] ?: true

    var accessToken: String?
        set(value) = if (value != null) settings.set(ACCESS_TOKEN, value) else settings.remove(ACCESS_TOKEN)
        get() = settings[ACCESS_TOKEN]

    var refreshToken: String?
        set(value) = if (value != null) settings.set(REFRESH_TOKEN, value) else settings.remove(REFRESH_TOKEN)
        get() = settings[REFRESH_TOKEN]

    var remoteUserId: Long?
        set(value) = if (value != null) settings.set(REMOTE_USER_ID, value) else settings.remove(REMOTE_USER_ID)
        get() = settings[REMOTE_USER_ID]

    var lastSyncAt: Long
        set(value) = settings.set(LAST_SYNC_AT, value)
        get() = settings[LAST_SYNC_AT] ?: 0L

    fun clearAuthTokens() {
        settings.remove(ACCESS_TOKEN)
        settings.remove(REFRESH_TOKEN)
        settings.remove(REMOTE_USER_ID)
    }
}
