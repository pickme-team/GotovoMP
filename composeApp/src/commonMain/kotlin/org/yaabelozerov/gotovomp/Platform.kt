package org.yaabelozerov.gotovomp

import app.cash.sqldelight.db.SqlDriver
import org.yaabelozerov.RecipeDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getLanguage(): String

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
