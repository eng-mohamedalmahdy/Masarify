package com.lightfeather.domain.repository

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.UserData

interface UserRepository {
    fun upsertUserData(userData: UserData): DomainResult<Unit>
}