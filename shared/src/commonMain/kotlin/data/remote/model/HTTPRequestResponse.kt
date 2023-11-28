package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class HTTPRequestResponse<T>(val data:T)
