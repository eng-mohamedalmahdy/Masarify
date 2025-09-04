package com.lightfeather.domain.repository

import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.UserData

interface UserRepository {

    fun getUserData(): DomainResult<UserData?>
    fun upsertUserData(userData: UserData): DomainResult<Unit>
    fun isDarkMode(): Boolean
    fun isDynamicColors(): Boolean
    fun getAppLanguage() : AppLanguage?

    fun toggleDarkMode()
    fun toggleDynamicColors()
    fun setAppLanguage(appLanguage: AppLanguage)
}