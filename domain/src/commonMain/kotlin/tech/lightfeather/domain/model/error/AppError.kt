package tech.lightfeather.domain.model.error

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppError {
    val message: String

    @Serializable
    data class InternalError(
        override val message: String,
    ) : AppError

    @Serializable
    data class ConflictError(
        override val message: String,
    ) : AppError

    @Serializable
    data class UpgradeRequired(
        override val message: String,
    ) : AppError
}
