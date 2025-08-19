package org.gotovo.gotovomp.util

import app.cash.sqldelight.db.SqlDriver

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getLanguage(): String

expect fun createDriver(): SqlDriver