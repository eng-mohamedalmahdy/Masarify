package tech.lightfeather.domain.repository

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val slug: String = PlatformsSlugs.ANDROID.slug
}

actual fun getPlatform(): Platform = AndroidPlatform()
