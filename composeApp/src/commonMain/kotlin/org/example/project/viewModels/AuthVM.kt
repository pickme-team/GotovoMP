package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

data class AuthVMState(
    val error: DomainError? = null
)

class AuthVM(
    val client: ApiClient = ApiClient(Net.client)
): ViewModel() {
    private val _state = MutableStateFlow(AuthVMState())
    val state = _state.asStateFlow()

    fun tryRegister(signUpRequest: SignUpRequest, callback: () -> Unit) {
        viewModelScope.launch {
            client.singUp(signUpRequest) unwrap {
                tryLogin(signUpRequest.phoneNumber, signUpRequest.password) {
                    callback()
                }
            } otherwise { err ->
                _state.update { it.copy(error = err) }
            }
        }
    }

    fun tryLogin(phoneNumber: String, password: String, callback: () -> Unit) {
        viewModelScope.launch {
            client.singIn(SignInWithPhoneNumberRequest(phoneNumber, password)) unwrap { res ->
                SettingsManager.token = res.token
                callback()
            } otherwise { err ->
                _state.update { it.copy(error = err) }
            }
        }
    }

    fun resetError() = _state.update { it.copy(error = null) }
}