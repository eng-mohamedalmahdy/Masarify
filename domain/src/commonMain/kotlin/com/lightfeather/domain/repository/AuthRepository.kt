package com.lightfeather.domain.repository

import com.lightfeather.domain.model.DomainResult

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): DomainResult<Unit>
    suspend fun login(email: String, password: String): DomainResult<Unit>
    suspend fun logout(): DomainResult<Unit>
    suspend fun refreshTokens(): DomainResult<Unit>
    fun isAuthenticated(): Boolean
}
