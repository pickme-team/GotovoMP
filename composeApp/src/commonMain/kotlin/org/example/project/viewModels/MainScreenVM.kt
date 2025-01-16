package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.DomainError
import org.example.project.data.network.ApiClient
import org.example.project.data.network.createHttpClient

data class MainScreenVMState(
    val name: String = "",
    val error: DomainError? = null,
)

class FeedScreenVM(
    private val api: ApiClient = ApiClient(createHttpClient())
) : ViewModel() {
    private val _state = MutableStateFlow(MainScreenVMState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
        }
    }
}
