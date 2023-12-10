package com.lightfeather.core.domain

sealed class DomainResult<T>(open val data: T?) {
    data class Success<T>(override val data: T) : DomainResult<T>(data)
    data class Failure<T>(val throwable: Throwable) : DomainResult<T>(null)
}

fun <R, T> T.toDomainResult(mapper: (input: T) -> R): DomainResult<R> =
    runCatching { DomainResult.Success(mapper(this)) }
        .getOrElse { DomainResult.Failure(it) }

fun <T> T.toDomainResult(): DomainResult<T> = DomainResult.Success(this)
