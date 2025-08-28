package com.lightfeather.domain.domain.transaction

data class InvalidTransaction(override val message: String) : Throwable(message)