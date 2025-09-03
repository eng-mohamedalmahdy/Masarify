package com.lightfeather.data.repository

import com.lightfeather.data.local.AppPreferences
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.UserData
import com.lightfeather.domain.model.runCatchingDomainResult
import com.lightfeather.domain.repository.UserRepository

class UserRepositoryImpl(
    private val preferences: AppPreferences
) : UserRepository {
    override fun upsertUserData(userData: UserData): DomainResult<Unit> {
        return runCatchingDomainResult {
            preferences.userData = userData
        }
    }
}