package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.local.settings.SettingsManager
import org.example.project.data.network.ApiClient
import org.example.project.data.network.Net
import org.example.project.data.network.model.SignUpRequest
import org.example.project.data.network.model.UserDTO
import org.example.project.domain.DomainError
import org.example.project.domain.GlobalEvent
import org.example.project.domain.UI
import org.example.project.domain.otherwise
import org.example.project.domain.unwrap
import org.example.project.nullIfBlank

data class ProfileVMState(
    val name: String = "",
    val surname: String = "",
    val username: String = "",
    val avatarUrl: String = "https://www.thesun.co.uk/wp-content/uploads/2022/08/OP-GORD.jpg?strip=all&quality=100&w=1920&h=1080&crop=1",
    val error: DomainError? = null,
)

fun UserDTO.toProfileVMState() = ProfileVMState(
    name = firstName.orEmpty(), surname = lastName.orEmpty(), username = username
)

sealed interface ProfileScreenEvent {
    data class Logout(val callback: () -> Unit) : ProfileScreenEvent
}

class ProfileVM(
    private val api: ApiClient = ApiClient(Net.client),
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileVMState())
    val state = _state.asStateFlow()

    init {
        subscribeToGlobalEvents()
    }

    private fun subscribeToGlobalEvents() {
        viewModelScope.launch {
            UI.GlobalEventChannel.collect { event ->
                when (event) {
                    GlobalEvent.Logout -> _state.update { ProfileVMState() }
                    is GlobalEvent.Login -> fetchUserData()
                }
            }
        }
    }

    private fun fetchUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            api.getUserData() unwrap { res ->
                _state.update { res.toProfileVMState() }
            } otherwise {
                _state.update { it.copy(error = it.error) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update { ProfileVMState() }
            SettingsManager.token = ""
            UI.GlobalEventChannel.emit(GlobalEvent.Logout)
        }
    }
}