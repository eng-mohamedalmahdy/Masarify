package tech.lightfeather.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.error.AppError

// Network errors require generic exception handling for graceful degradation
@Suppress("TooGenericExceptionCaught")
class DeviceApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun registerToken(
        token: String,
        platform: String,
    ): DomainResult<Unit> =
        try {
            val response =
                httpClient
                    .post("$baseUrl/devices/register") {
                        contentType(ContentType.Application.Json)
                        setBody(RegisterDeviceRequest(token = token, platform = platform))
                    }.body<DeviceApiResponse<Unit>>()
            if (response.success) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Failure(AppError.InternalError(response.message ?: "Device registration failed"))
            }
        } catch (e: Exception) {
            Napier.e("Device token registration failed", e, tag = "DeviceApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Device registration failed"))
        }
}

@Serializable
private data class RegisterDeviceRequest(
    val token: String,
    val platform: String,
)

@Serializable
private data class DeviceApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
)
