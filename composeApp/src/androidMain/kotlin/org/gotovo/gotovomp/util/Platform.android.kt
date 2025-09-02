package org.gotovo.gotovomp.util

import android.os.Build
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.yaabelozerov.RecipeDatabase

import java.util.Locale

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun getLanguage(): String {
    val language = Locale.getDefault().language
    return language
}

actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(RecipeDatabase.Schema, android.app.Application(), "Recipe.db")
}

val androidModule = module {
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
} // респект