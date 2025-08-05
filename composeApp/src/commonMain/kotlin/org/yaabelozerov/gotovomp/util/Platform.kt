package org.yaabelozerov.gotovomp.util

import app.cash.sqldelight.db.SqlDriver
import org.yaabelozerov.RecipeDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getLanguage(): String

expect fun createDriver(): SqlDriver