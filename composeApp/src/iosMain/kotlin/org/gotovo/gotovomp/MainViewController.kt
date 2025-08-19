package org.gotovo.gotovomp

import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
fun MainViewController() = ComposeUIViewController {
    if (Platform.isDebugBinary) Napier.base(DebugAntilog())

    App()
}