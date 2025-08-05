package org.yaabelozerov.gotovomp.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.yaabelozerov.RecipeDatabase
import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun getLanguage(): String {
    // Get the first preferred language, or fallback to "en"
    val languages = NSLocale.preferredLanguages
    return if (languages.isNotEmpty()) languages[0] as String else "en"
}

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(RecipeDatabase.Schema, "Recipe.db")
}

val iosModule = module {
    single<NetworkMonitor> { IosNetworkMonitor() }
}