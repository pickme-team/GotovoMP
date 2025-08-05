package org.yaabelozerov.gotovomp.util

import kotlinx.coroutines.flow.Flow

class IosNetworkMonitor: NetworkMonitor {
    override val isOnline: Flow<Boolean>
        get() = TODO("Not yet implemented")
}