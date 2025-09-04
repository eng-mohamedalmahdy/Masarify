package com.lightfeather.domain.model.error

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppError {

    val message: String

    @Serializable
    data class InternalError(override val message: String) : AppError


}