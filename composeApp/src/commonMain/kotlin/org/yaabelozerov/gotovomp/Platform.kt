package org.yaabelozerov.gotovomp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform