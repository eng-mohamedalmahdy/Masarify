package com.lightfeather.core.domain.transaction

data class InvalidTransaction(override val message: String) : Throwable(message)