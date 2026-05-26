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
        authApi.register(name, email, password).mapSuspend { tokens ->
            preferences.accessToken = tokens.accessToken
            preferences.refreshToken = tokens.refreshToken
            preferences.remoteUserId = tokens.userId
            val existing = preferences.userData
            preferences.userData = existing?.copy(email = email, remoteUserId = tokens.userId)
            authApi.me().getOrNull()?.let { me -> preferences.isEmailVerified = me.isEmailVerified }
        }

    override suspend fun login(
        email: String,
        password: String,
    ): DomainResult<Unit> =
        authApi.login(email, password).mapSuspend { tokens ->
            preferences.accessToken = tokens.accessToken
            preferences.refreshToken = tokens.refreshToken
            preferences.remoteUserId = tokens.userId
            val existing = preferences.userData
            preferences.userData = existing?.copy(email = email, remoteUserId = tokens.userId)
            authApi.me().getOrNull()?.let { me -> preferences.isEmailVerified = me.isEmailVerified }
        }

    override suspend fun logout(): DomainResult<Unit> {
        val token = preferences.refreshToken ?: return DomainResult.Success(Unit)
        val result = authApi.logout(token)
        preferences.clearAuthTokens()
        return result
    }

    override suspend fun logoutAll(): DomainResult<Unit> {
        val result = authApi.logoutAll()
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

    override fun isEmailVerified(): Boolean = preferences.isEmailVerified

    override suspend fun verifyEmail(token: String): DomainResult<Unit> {
        val result = authApi.verifyEmail(token)
        if (result is DomainResult.Success) {
            preferences.isEmailVerified = true
        }
        return result
    }

    override suspend fun resendVerification(): DomainResult<Unit> = authApi.resendVerification()

    override suspend fun forgotPassword(email: String): DomainResult<Unit> = authApi.forgotPassword(email)

    override suspend fun resetPassword(
        token: String,
        newPassword: String,
    ): DomainResult<Unit> = authApi.resetPassword(token, newPassword)
}
