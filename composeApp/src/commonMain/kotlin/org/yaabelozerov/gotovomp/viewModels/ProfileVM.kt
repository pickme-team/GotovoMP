package org.yaabelozerov.gotovomp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.yaabelozerov.gotovomp.data.local.settings.SettingsManager
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.data.network.model.UserDTO
import org.yaabelozerov.gotovomp.domain.DomainError
import org.yaabelozerov.gotovomp.domain.GlobalEvent
import org.yaabelozerov.gotovomp.domain.UI
import org.yaabelozerov.gotovomp.domain.otherwise
import org.yaabelozerov.gotovomp.domain.unwrap

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
    private val api: ApiClient
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileVMState())
    val state = _state.asStateFlow()

    init {
        subscribeToGlobalEvents()
    }

    private fun subscribeToGlobalEvents() {
        viewModelScope.launch {
            UI.GlobalEventFlow.collect { event ->
                when (event) {
                    GlobalEvent.Logout -> _state.update { ProfileVMState() }
                    GlobalEvent.Login -> fetchUserData()
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
            UI.GlobalEventFlow.emit(GlobalEvent.Logout)
        }
    }
}