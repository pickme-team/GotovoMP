package org.yaabelozerov.gotovomp.domain

import kotlinx.coroutines.flow.MutableSharedFlow

sealed interface GlobalEvent {
    data object Login: GlobalEvent
    data object Logout: GlobalEvent
}

object UI {
    val GlobalEventFlow = MutableSharedFlow<GlobalEvent>(replay = 1)
}