package com.lightfeather.core.domain

import kotlin.native.concurrent.ThreadLocal

data class Currency(
    val id: Int,
    val name: String,
    val sign: String,
) {
    @ThreadLocal
    companion object {
        var exchangeRates: List<List<Double?>> = listOf()
    }
}
