package com.lightfeather.domain.repository

import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.UserData

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
}
