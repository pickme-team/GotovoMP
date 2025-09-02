package org.gotovo.gotovomp.util

import kotlinx.coroutines.flow.Flow

class IosNetworkMonitor: NetworkMonitor {
    override val isOnline: Flow<Boolean>
        get() = TODO("Not yet implemented")
}