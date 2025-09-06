package com.lightfeather.masarify

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val slug: String = PlatformsSlugs.DESKTOP.slug
}

actual fun getPlatform(): Platform = JVMPlatform()