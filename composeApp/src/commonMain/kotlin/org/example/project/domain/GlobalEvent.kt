package org.example.project.domain

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed interface GlobalEvent {
    data object Login: GlobalEvent
    data object Logout: GlobalEvent
}

object UI {
    val GlobalEventChannel = MutableSharedFlow<GlobalEvent>()
}