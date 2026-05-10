package tech.lightfeather.domain.repository

interface Platform {
    val name: String
    val slug: String
}

enum class PlatformsSlugs(
    val slug: String,
) {
    IOS("iOS"),
    ANDROID("Android"),
    DESKTOP("JVM"),
    WEB("WASM"),
    ;

    fun isMobile() = this == IOS || this == ANDROID
}

fun Platform.asSlug() = PlatformsSlugs.entries.find { it.slug == this.slug }

expect fun getPlatform(): Platform
