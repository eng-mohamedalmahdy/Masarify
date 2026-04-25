package com.lightfeather.data.remote

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.error.AppError
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Network errors require generic exception handling for graceful degradation
@Suppress("TooGenericExceptionCaught")
class AuthApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun register(
        name: String,
        email: String,
        password: String,
    ): DomainResult<AuthTokensDto> =
        try {
            val response =
                httpClient
                    .post("$baseUrl/auth/register") {
                        contentType(ContentType.Application.Json)
                        setBody(RegisterRequest(name, email, password))
                    }.body<AuthApiResponse<AuthTokensDto>>()
            if (response.success && response.data != null) {
                DomainResult.Success(response.data)
            } else {
                DomainResult.Failure(AppError.InternalError(response.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Napier.e("Register failed", e, tag = "AuthApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Registration failed"))
        }

    suspend fun login(
        email: String,
        password: String,
    ): DomainResult<AuthTokensDto> =
        try {
            val response =
                httpClient
                    .post("$baseUrl/auth/login") {
                        contentType(ContentType.Application.Json)
                        setBody(LoginRequest(email, password))
                    }.body<AuthApiResponse<AuthTokensDto>>()
            if (response.success && response.data != null) {
                DomainResult.Success(response.data)
            } else {
                DomainResult.Failure(AppError.InternalError(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Napier.e("Login failed", e, tag = "AuthApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Login failed"))
        }

    suspend fun refresh(refreshToken: String): DomainResult<AuthTokensDto> =
        try {
            val response =
                httpClient
                    .post("$baseUrl/auth/refresh") {
                        contentType(ContentType.Application.Json)
                        setBody(RefreshRequest(refreshToken))
                    }.body<AuthApiResponse<AuthTokensDto>>()
            if (response.success && response.data != null) {
                DomainResult.Success(response.data)
            } else {
                DomainResult.Failure(AppError.InternalError(response.message ?: "Token refresh failed"))
            }
        } catch (e: Exception) {
            Napier.e("Token refresh failed", e, tag = "AuthApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Token refresh failed"))
        }

    suspend fun logout(refreshToken: String): DomainResult<Unit> =
        try {
            val response =
                httpClient
                    .post("$baseUrl/auth/logout") {
                        contentType(ContentType.Application.Json)
                        setBody(LogoutRequest(refreshToken))
                    }.body<AuthApiResponse<Unit>>()
            if (response.success) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Failure(AppError.InternalError(response.message ?: "Logout failed"))
            }
        } catch (e: Exception) {
            Napier.e("Logout failed", e, tag = "AuthApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Logout failed"))
        }
}

@Serializable
data class AuthTokensDto(
    @SerialName("token") val accessToken: String,
    val refreshToken: String,
    val userId: Long,
)

@Serializable
private data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
private data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
private data class RefreshRequest(
    val refreshToken: String,
)

@Serializable
private data class LogoutRequest(
    val refreshToken: String,
)

@Serializable
private data class AuthApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
)
