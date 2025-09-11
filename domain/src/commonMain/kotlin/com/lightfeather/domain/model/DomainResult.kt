package com.lightfeather.domain.model

import com.lightfeather.domain.model.error.AppError

sealed class DomainResult<T>(
    open val data: T?,
) {
    data class Success<T>(
        override val data: T,
    ) : DomainResult<T>(data)

    data class Failure<T>(
        val error: AppError,
    ) : DomainResult<T>(null)

    fun fold(
        onSuccess: (T) -> Unit = {},
        onFailure: (AppError) -> Unit = {},
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is DomainResult.Failure -> onFailure(error)
        }
    }

    suspend fun foldSuspend(
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (AppError) -> Unit = {},
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is DomainResult.Failure -> onFailure(error)
        }
    }

    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            is DomainResult.Failure -> null
        }

    fun getOrElse(default: T): T =
        when (this) {
            is Success -> data
            is DomainResult.Failure -> default
        }

    inline fun <R> foldResult(
        onSuccess: (T) -> R,
        onFailure: (AppError) -> R,
    ): R =
        when (this) {
            is Success -> onSuccess(data)
            is DomainResult.Failure -> onFailure(error)
        }

    fun <R, S> combine(
        other: DomainResult<R>,
        transform: (T, R) -> S,
    ): DomainResult<S> =
        when (this) {
            is DomainResult.Success ->
                when (other) {
                    is DomainResult.Success -> DomainResult.Success(transform(data, other.data))
                    is DomainResult.Failure -> DomainResult.Failure(other.error)
                }

            is DomainResult.Failure -> DomainResult.Failure(error)
        }

    fun <R> map(transform: (T) -> R): DomainResult<R> =
        when (this) {
            is DomainResult.Success -> DomainResult.Success(transform(data))
            is DomainResult.Failure -> DomainResult.Failure(error)
        }

    suspend fun <R> mapSuspend(transform: suspend (T) -> R): DomainResult<R> =
        when (this) {
            is DomainResult.Success -> DomainResult.Success(transform(data))
            is DomainResult.Failure -> DomainResult.Failure(error)
        }

    fun <R> flatMap(transform: (T) -> DomainResult<R>): DomainResult<R> =
        when (this) {
            is DomainResult.Success -> {
                try {
                    transform(data)
                } catch (e: Exception) {
                    DomainResult.Failure(AppError.InternalError(e.message.orEmpty()))
                }
            }
            is DomainResult.Failure -> DomainResult.Failure(error)
        }

    suspend fun <R> flatMapSuspend(transform: suspend (T) -> DomainResult<R>): DomainResult<R> =
        when (this) {
            is DomainResult.Success -> {
                try {
                    transform(data)
                } catch (e: Exception) {
                    DomainResult.Failure(AppError.InternalError(e.message.orEmpty()))
                }
            }
            is DomainResult.Failure -> DomainResult.Failure(error)
        }

    companion object {
        fun <T, R, S> combine(
            first: DomainResult<T>,
            second: DomainResult<R>,
            transform: (T, R) -> S,
        ): DomainResult<S> = first.combine(second, transform)
    }
}

fun <R, T> T.toDomainResult(mapper: (input: T) -> R): DomainResult<R> =
    runCatching { DomainResult.Success(mapper(this)) }
        .getOrElse { DomainResult.Failure(AppError.InternalError(it.message.orEmpty())) }

fun <T> T.toDomainResult(): DomainResult<T> = DomainResult.Success(this)

fun <T> runCatchingDomainResult(block: () -> T): DomainResult<T> =
    runCatching(block).fold(
        { DomainResult.Success(it) },
        { DomainResult.Failure(AppError.InternalError(it.message.orEmpty())) },
    )

suspend fun <T> runCatchingDomainResultSuspend(block: suspend () -> T): DomainResult<T> =
    runCatching {
        block()
    }.fold(
        { DomainResult.Success(it) },
        { DomainResult.Failure(AppError.InternalError(it.message.orEmpty())) },
    )
