package com.lightfeather.data.repository

import com.lightfeather.data.local.AppPreferences
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.UserData
import com.lightfeather.domain.model.runCatchingDomainResult
import com.lightfeather.domain.repository.UserRepository

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
}
