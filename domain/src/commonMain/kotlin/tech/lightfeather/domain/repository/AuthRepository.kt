package tech.lightfeather.domain.repository

import tech.lightfeather.domain.model.DomainResult

interface AuthRepository {
    suspend fun register(
        name: String,
        email: String,
        password: String,
    ): DomainResult<Unit>

    suspend fun login(
        email: String,
        password: String,
    ): DomainResult<Unit>

    suspend fun logout(): DomainResult<Unit>

    suspend fun logoutAll(): DomainResult<Unit>

    suspend fun refreshTokens(): DomainResult<Unit>

    fun isAuthenticated(): Boolean

    fun isEmailVerified(): Boolean

    suspend fun verifyEmail(token: String): DomainResult<Unit>

    suspend fun resendVerification(): DomainResult<Unit>

    suspend fun forgotPassword(email: String): DomainResult<Unit>

    suspend fun resetPassword(
        token: String,
        newPassword: String,
    ): DomainResult<Unit>
}
