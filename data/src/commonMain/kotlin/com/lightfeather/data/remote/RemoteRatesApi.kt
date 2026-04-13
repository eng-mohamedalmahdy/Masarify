package com.lightfeather.data.remote

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.error.AppError
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

// Network errors require generic exception handling for graceful degradation
@Suppress("TooGenericExceptionCaught")
class RemoteRatesApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getRemoteRates(): DomainResult<List<RemoteExchangeRateDto>> =
        try {
            val rates =
                httpClient
                    .get("$baseUrl/api/exchange-rates/remote")
                    .body<ApiResponse<List<RemoteExchangeRateDto>>>()
            DomainResult.Success(rates.data ?: emptyList())
        } catch (e: Exception) {
            Napier.e("Failed to fetch remote exchange rates", e, tag = "RemoteRatesApi")
            DomainResult.Failure(AppError.InternalError(e.message ?: "Failed to fetch remote rates"))
        }
}

@Serializable
data class RemoteExchangeRateDto(
    val baseCurrencyCode: String,
    val targetCurrencyCode: String,
    val rate: Double,
    val fetchedAt: Long,
)

@Serializable
private data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
)
