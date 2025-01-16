package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.DomainError
import org.example.project.domain.otherwise
import org.example.project.domain.unwrap
import org.example.project.network.ApiClient
import org.example.project.network.createHttpClient

data class MainScreenVMState(
    val name: String = "",
    val error: DomainError? = null,
)

class FeedScreenVM(
    private val api: ApiClient = ApiClient(createHttpClient())
) : ViewModel() {
    private val _state = MutableStateFlow(MainScreenVMState())
    val state = _state.asStateFlow()

    private suspend fun fetchName() {
        api.ping() unwrap { res ->
            _state.update { it.copy(name = res.name) }
        } otherwise { err ->
            _state.update { it.copy(error = err) }
        }
    }

    init {
        viewModelScope.launch {
            fetchName()
        }
    }
}
