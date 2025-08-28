package com.lightfeather.domain.domain

sealed class DomainResult<T>(open val data: T?) {
    data class Success<T>(override val data: T) : DomainResult<T>(data)
    data class Failure<T>(val error: AppError) : DomainResult<T>(null)

    fun fold(onSuccess: (T) -> Unit = {}, onFailure: (AppError) -> Unit = {}) {
        when (this) {
            is Success -> onSuccess(data)
            is DomainResult.Failure -> onFailure(error)
        }
    }

    suspend fun foldSuspend(
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (AppError) -> Unit = {}
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is DomainResult.Failure -> onFailure(error)
        }
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is DomainResult.Failure -> null
    }


    fun getOrElse(default: T): T = when (this) {
        is Success -> data
        is DomainResult.Failure -> default
    }



    inline fun <R> foldResult(
        onSuccess: (T) -> R,
        onFailure: (AppError) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is DomainResult.Failure -> onFailure(error)
    }
}



fun <R, T> T.toDomainResult(mapper: (input: T) -> R): DomainResult<R> =
    runCatching { DomainResult.Success(mapper(this)) }
        .getOrElse { DomainResult.Failure(AppError.InternalError(it.message.orEmpty())) }

fun <T> T.toDomainResult(): DomainResult<T> = DomainResult.Success(this)
