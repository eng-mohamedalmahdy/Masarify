package tech.lightfeather.domain.repository

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val slug: String = PlatformsSlugs.WEB.slug
}

actual fun getPlatform(): Platform = WasmPlatform()
