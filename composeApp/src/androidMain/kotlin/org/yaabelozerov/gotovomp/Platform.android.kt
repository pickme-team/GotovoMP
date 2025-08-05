package org.yaabelozerov.gotovomp

import android.content.Context
import android.os.Build
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
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

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(RecipeDatabase.Schema, context, "Recipe.db")
    }
}