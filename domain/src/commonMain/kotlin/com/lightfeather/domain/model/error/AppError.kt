package com.lightfeather.domain.model.error

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppError {

    @Serializable
    data class InternalError(val message: String) : AppError


}