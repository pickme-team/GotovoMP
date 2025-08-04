package org.yaabelozerov.gotovomp

import android.os.Build
import java.util.Locale

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun getLanguage(): String {
    val language = Locale.getDefault().language
    return language
}