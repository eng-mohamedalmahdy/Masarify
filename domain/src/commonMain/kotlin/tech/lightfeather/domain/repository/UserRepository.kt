package tech.lightfeather.domain.repository

import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.UserData

interface UserRepository {
    fun getUserData(): DomainResult<UserData?>

    fun upsertUserData(userData: UserData): DomainResult<Unit>

    fun isDarkMode(): Boolean

    fun isDynamicColors(): Boolean

    fun getAppLanguage(): AppLanguage?

    fun toggleDarkMode()

    fun toggleDynamicColors()

    fun setAppLanguage(appLanguage: AppLanguage)

    fun getUserSavedColors(): List<String>

    fun saveColor(color: String)

    /**
     * Checks if application data (currencies, bank names, categories) has been seeded.
     *
     * @return true if data has been seeded, false otherwise
     */
    fun isDataSeeded(): Boolean

    /**
     * Marks application data as seeded to prevent duplicate seeding on subsequent launches.
     */
    fun markDataAsSeeded()

    /** Returns true if biometric authentication is enabled by the user. */
    fun isBiometricEnabled(): Boolean

    /** Enables or disables biometric authentication. */
    fun setBiometricEnabled(enabled: Boolean)

    /** Returns true if the biometric suggestion dialog has already been shown. */
    fun hasShownBiometricSuggestion(): Boolean

    /** Marks the biometric suggestion dialog as shown. */
    fun markBiometricSuggestionShown()

    /** Returns true if auto-sync of exchange rates is enabled (defaults to true). */
    fun isAutoSyncRatesEnabled(): Boolean

    /** Enables or disables automatic exchange rate syncing from the remote server. */
    fun setAutoSyncRatesEnabled(enabled: Boolean)

    /** Returns true if auto-sync of user data to the cloud is enabled (defaults to true). */
    fun isAutoSyncDataEnabled(): Boolean

    /** Enables or disables automatic user data syncing to the cloud. */
    fun setAutoSyncDataEnabled(enabled: Boolean)

    fun getLastSyncAt(): Long

    fun setLastSyncAt(ts: Long)

    fun isLoggedIn(): Boolean

    fun syncFcmToken(token: String, platform: String)

    fun getFcmToken(): String?

    fun getFcmPlatform(): String?

    fun clearFcmToken()
}
