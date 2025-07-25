package org.yaabelozerov.gotovomp.data.local.settings

import com.russhwolf.settings.Settings

object SettingsManager {
    private val settings = Settings()

    var token: String
        get() = settings.getString("token", "")
        set(value) = settings.putString("token", value)
}