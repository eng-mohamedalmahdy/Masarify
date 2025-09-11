package com.lightfeather.masarify

import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val slug: String = PlatformsSlugs.IOS.slug
}

actual fun getPlatform(): Platform = IOSPlatform()
