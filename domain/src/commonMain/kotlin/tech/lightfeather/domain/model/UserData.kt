package tech.lightfeather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userName: String,
    val email: String? = null,
    val remoteUserId: Long? = null,
)
