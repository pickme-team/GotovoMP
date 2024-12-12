package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.network.ApiClient
import org.example.project.network.util.onSuccess

data class MainScreenVMState(
    val name: String? = null
)

class FeedScreenVM(
    private val api: ApiClient
): ViewModel() {
    private val _state = MutableStateFlow(MainScreenVMState())
    val state = _state.asStateFlow()

    private suspend fun fetchName() {
        try {
            api.ping().onSuccess {
                _state.update { state ->
                    state.copy(
                        name = it
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        viewModelScope.launch {
            fetchName()
        }
    }

}
