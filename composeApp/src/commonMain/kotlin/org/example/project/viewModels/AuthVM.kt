package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.SignInWithPhoneNumberRequest
import org.example.project.data.SignUpRequest
import org.example.project.data.toSignInWithPhoneNumberRequest
import org.example.project.domain.DomainError
import org.example.project.domain.otherwise
import org.example.project.domain.unwrap
import org.example.project.network.ApiClient
import org.example.project.network.createHttpClient

data class AuthVMState(
    val error: DomainError? = null
)

class AuthVM(
    val client: ApiClient = ApiClient(createHttpClient())
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
            client.singIn(SignInWithPhoneNumberRequest(phoneNumber, password)) unwrap {
                callback()
            } otherwise { err ->
                _state.update { it.copy(error = err) }
            }
        }
    }

    fun resetError() = _state.update { it.copy(error = null) }
}