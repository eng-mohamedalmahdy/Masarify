package tech.lightfeather.data.repository

import tech.lightfeather.data.local.AppPreferences
import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.UserData
import tech.lightfeather.domain.model.runCatchingDomainResult
import tech.lightfeather.domain.repository.UserRepository

class UserRepositoryImpl(
    private val preferences: AppPreferences,
) : UserRepository {
    override fun getUserData(): DomainResult<UserData?> =
        runCatchingDomainResult {
            preferences.userData
        }

    override fun upsertUserData(userData: UserData): DomainResult<Unit> =
        runCatchingDomainResult {
            preferences.userData = userData
        }

    override fun isDarkMode(): Boolean = preferences.isDarkMode

    override fun isDynamicColors(): Boolean = preferences.isDynamicColors

    override fun getAppLanguage(): AppLanguage? = preferences.appLanguage

    override fun toggleDarkMode() {
        preferences.isDarkMode = !preferences.isDarkMode
    }

    override fun toggleDynamicColors() {
        preferences.isDynamicColors = !preferences.isDynamicColors
    }

    override fun setAppLanguage(appLanguage: AppLanguage) {
        preferences.appLanguage = appLanguage
    }

    override fun getUserSavedColors(): List<String> = preferences.userSavedColors

    override fun saveColor(color: String) {
        preferences.userSavedColors = (listOf(color) + preferences.userSavedColors).toSet().take(10)
    }

    override fun isDataSeeded(): Boolean = preferences.isDataSeeded

    override fun markDataAsSeeded() {
        preferences.isDataSeeded = true
    }

    override fun isBiometricEnabled(): Boolean = preferences.isBiometricEnabled

    override fun setBiometricEnabled(enabled: Boolean) {
        preferences.isBiometricEnabled = enabled
    }

    override fun hasShownBiometricSuggestion(): Boolean = preferences.hasShownBiometricSuggestion

    override fun markBiometricSuggestionShown() {
        preferences.hasShownBiometricSuggestion = true
    }

    override fun isAutoSyncRatesEnabled(): Boolean = preferences.isAutoSyncRatesEnabled

    override fun setAutoSyncRatesEnabled(enabled: Boolean) {
        preferences.isAutoSyncRatesEnabled = enabled
    }

    override fun isAutoSyncDataEnabled(): Boolean = preferences.isAutoSyncDataEnabled

    override fun setAutoSyncDataEnabled(enabled: Boolean) {
        preferences.isAutoSyncDataEnabled = enabled
    }

    override fun getLastSyncAt(): Long = preferences.lastSyncAt

    override fun setLastSyncAt(ts: Long) {
        preferences.lastSyncAt = ts
    }

    override fun isLoggedIn(): Boolean = preferences.remoteUserId != null

    override fun syncFcmToken(token: String, platform: String) {
        preferences.fcmToken = token
        preferences.fcmPlatform = platform
    }

    override fun getFcmToken(): String? = preferences.fcmToken

    override fun getFcmPlatform(): String? = preferences.fcmPlatform

    override fun clearFcmToken() {
        preferences.fcmToken = null
        preferences.fcmPlatform = null
    }
}
