package com.lightfeather.domain.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppError {

    @Serializable
    data class InternalError(val message: String) : AppError


}
