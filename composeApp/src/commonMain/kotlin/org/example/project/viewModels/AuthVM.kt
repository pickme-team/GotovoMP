package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.local.settings.SettingsManager
import org.example.project.data.network.model.SignInWithPhoneNumberRequest
import org.example.project.data.network.model.SignUpRequest
import org.example.project.domain.DomainError
import org.example.project.domain.otherwise
import org.example.project.domain.unwrap
import org.example.project.data.network.ApiClient
import org.example.project.data.network.Net
import org.example.project.domain.GlobalEvent
import org.example.project.domain.UI
import org.example.project.nullIfBlank

data class AuthVMState(
    val checkedForLogin: Boolean = false,
    val error: DomainError? = null
)

class AuthVM(
    val client: ApiClient = ApiClient(Net.client)
): ViewModel() {
    private val _state = MutableStateFlow(AuthVMState())
    val state = _state.asStateFlow()

    fun checkSavedLogin() {
        SettingsManager.token.nullIfBlank()?.let {
            viewModelScope.launch {
                UI.GlobalEventChannel.emit(GlobalEvent.Login)
            }
        }
        _state.update { it.copy(checkedForLogin = true) }
    }

    fun tryRegister(fullUserDTO: SignUpRequest) {
        viewModelScope.launch {
            client.singUp(fullUserDTO) unwrap {
                tryLogin(fullUserDTO.phoneNumber, fullUserDTO.password)
            } otherwise { err ->
                _state.update { it.copy(error = err) }
            }
        }
    }

    fun tryLogin(phoneNumber: String, password: String) {
        val scope = viewModelScope
        viewModelScope.launch {
            client.singIn(SignInWithPhoneNumberRequest(phoneNumber, password)) unwrap { res ->
                println("Successful login with phone number $phoneNumber and password $password")
                scope.launch {
                    SettingsManager.token = res.token
                    UI.GlobalEventChannel.emit(GlobalEvent.Login)
                }
            } otherwise { err ->
                _state.update { it.copy(error = err) }
            }
        }
    }

    fun resetError() = _state.update { it.copy(error = null) }
}