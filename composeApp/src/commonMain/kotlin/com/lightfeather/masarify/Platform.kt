package com.lightfeather.masarify

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform