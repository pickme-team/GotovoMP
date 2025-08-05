package org.yaabelozerov.gotovomp

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun getLanguage(): String {
    // Get the first preferred language, or fallback to "en"
    val languages = NSLocale.preferredLanguages
    return if (languages.isNotEmpty()) languages[0] as String else "en"
}