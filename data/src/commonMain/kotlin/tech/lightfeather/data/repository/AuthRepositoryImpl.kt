package tech.lightfeather.data.repository

import tech.lightfeather.data.local.AppPreferences
import tech.lightfeather.data.remote.AuthApi
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val preferences: AppPreferences,
) : AuthRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): DomainResult<Unit> =
        authApi.register(name, email, password).map { tokens ->
            preferences.accessToken = tokens.accessToken
            preferences.refreshToken = tokens.refreshToken
            preferences.remoteUserId = tokens.userId
            val existing = preferences.userData
            preferences.userData = existing?.copy(email = email, remoteUserId = tokens.userId)
        }

    override suspend fun login(
        email: String,
        password: String,
    ): DomainResult<Unit> =
        authApi.login(email, password).map { tokens ->
            preferences.accessToken = tokens.accessToken
            preferences.refreshToken = tokens.refreshToken
            preferences.remoteUserId = tokens.userId
            val existing = preferences.userData
            preferences.userData = existing?.copy(email = email, remoteUserId = tokens.userId)
        }

    override suspend fun logout(): DomainResult<Unit> {
        val token = preferences.refreshToken ?: return DomainResult.Success(Unit)
        val result = authApi.logout(token)
        preferences.clearAuthTokens()
        return result
    }

    override suspend fun refreshTokens(): DomainResult<Unit> {
        val token =
            preferences.refreshToken ?: return DomainResult.Failure(
                tech.lightfeather.domain.model.error.AppError
                    .InternalError("No refresh token"),
            )
        return authApi.refresh(token).map { tokens ->
            preferences.accessToken = tokens.accessToken
            preferences.refreshToken = tokens.refreshToken
        }
    }

    override fun isAuthenticated(): Boolean = preferences.accessToken != null
}
