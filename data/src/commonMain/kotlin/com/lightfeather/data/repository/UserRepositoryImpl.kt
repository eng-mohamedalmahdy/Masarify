package com.lightfeather.data.repository

import com.lightfeather.data.local.AppPreferences
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.UserData
import com.lightfeather.domain.model.runCatchingDomainResult
import com.lightfeather.domain.repository.UserRepository

class UserRepositoryImpl(
    private val preferences: AppPreferences
) : UserRepository {
    override fun getUserData(): DomainResult<UserData?> {
        return runCatchingDomainResult {
            preferences.userData
        }
    }

    override fun upsertUserData(userData: UserData): DomainResult<Unit> {
        return runCatchingDomainResult {
            preferences.userData = userData
        }
    }

    override fun isDarkMode(): Boolean {
        return preferences.isDarkMode
    }

    override fun isDynamicColors(): Boolean {
        return preferences.isDynamicColors
    }

    override fun getAppLanguage(): AppLanguage? {
        return preferences.appLanguage
    }

    override fun toggleDarkMode() {
        preferences.isDarkMode = !preferences.isDarkMode
    }

    override fun toggleDynamicColors() {
        preferences.isDynamicColors = !preferences.isDynamicColors
    }

    override fun setAppLanguage(appLanguage: AppLanguage) {
        preferences.appLanguage = appLanguage
    }
}