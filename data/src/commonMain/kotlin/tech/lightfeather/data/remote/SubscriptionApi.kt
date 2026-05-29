package tech.lightfeather.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.SubscriptionPlan
import tech.lightfeather.domain.model.SubscriptionStatus
import tech.lightfeather.domain.model.SubscriptionStatusType
import tech.lightfeather.domain.model.error.AppError

// Network errors require generic exception handling for graceful degradation
@Suppress("TooGenericExceptionCaught")
class SubscriptionApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getStatus(): DomainResult<SubscriptionStatus> =
        try {
            val response =
                httpClient
                    .get("$baseUrl/subscription/status")
                    .body<SubscriptionApiResponse<SubscriptionStatusDto>>()
            if (response.success && response.data != null) {
                DomainResult.Success(response.data.toDomain())
            } else {
                DomainResult.Failure(AppError.InternalError(response.message ?: "Failed to fetch subscription status"))
            }
        } catch (e: ClientRequestException) {
            Napier.e("Subscription status failed", e, tag = "SubscriptionApi")
            if (e.response.status == HttpStatusCode(402, "Payment Required")) {
                DomainResult.Success(SubscriptionStatus.FREE_DEFAULT)
            } else {
                DomainResult.Failure(AppError.InternalError(e.message))
            }
        } catch (e: Exception) {
            Napier.e("Subscription status failed", e, tag = "SubscriptionApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Failed to fetch subscription status"))
        }

    suspend fun linkCustomer(rcCustomerId: String): DomainResult<Unit> =
        try {
            httpClient.post("$baseUrl/subscription/link") {
                contentType(ContentType.Application.Json)
                setBody(LinkRevenueCatRequestDto(rcCustomerId))
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            Napier.e("Subscription link failed", e, tag = "SubscriptionApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Failed to link subscription"))
        }
}

private fun SubscriptionStatusDto.toDomain() =
    SubscriptionStatus(
        plan = runCatching { SubscriptionPlan.valueOf(plan) }.getOrElse { SubscriptionPlan.FREE },
        status = runCatching { SubscriptionStatusType.valueOf(status) }.getOrElse { SubscriptionStatusType.NONE },
        expiresAt = expiresAt,
    )

@Serializable
private data class SubscriptionApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
)

@Serializable
private data class SubscriptionStatusDto(
    val plan: String,
    val status: String,
    @SerialName("expiresAt") val expiresAt: Long? = null,
    val isProActive: Boolean,
)

@Serializable
private data class LinkRevenueCatRequestDto(
    val revenueCatCustomerId: String,
)
