package org.yaabelozerov.gotovomp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.yaabelozerov.gotovomp.data.local.settings.SettingsManager
import org.yaabelozerov.gotovomp.data.network.model.SignInWithPhoneNumberRequest
import org.yaabelozerov.gotovomp.data.network.model.SignUpRequest
import org.yaabelozerov.gotovomp.domain.DomainError
import org.yaabelozerov.gotovomp.data.network.ApiClient
import org.yaabelozerov.gotovomp.domain.GlobalEvent
import org.yaabelozerov.gotovomp.domain.UI
import org.yaabelozerov.gotovomp.domain.onError
import org.yaabelozerov.gotovomp.domain.onSuccess
import org.yaabelozerov.gotovomp.nullIfBlank

class AuthVM(
    private val client: ApiClient,
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        checkSavedLogin()
    }

    fun checkSavedLogin() {
        viewModelScope.launch {
            SettingsManager.token.nullIfBlank()?.let {
                UI.GlobalEventFlow.emit(GlobalEvent.Login)
            } ?: _state.update { it.copy(checkedForLogin = true) }
        }
    }

    fun tryRegister(
        firstName: String,
        lastName: String,
        username: String,
        phoneNumber: String,
        password: String,
    ) {
        viewModelScope.launch {
            val request = SignUpRequest(
                firstName = firstName,
                lastName = lastName,
                username = username,
                phoneNumber = phoneNumber,
                password = password
            )
            client.singUp(request).onSuccess {
                tryLogin(request.phoneNumber, request.password)
            }.onError { err ->
                _state.update { it.copy(error = err) }
            }
        }
    }

    fun tryLogin(phoneNumber: String, password: String) {
        val scope = viewModelScope
        viewModelScope.launch {
            client.singIn(SignInWithPhoneNumberRequest(phoneNumber, password)).onSuccess { res ->
                SettingsManager.token = res.token
                scope.launch {
                    UI.GlobalEventFlow.emit(GlobalEvent.Login)
                }
            }.onError { err ->
                _state.update { it.copy(error = err) }
            }
        }
    }

    fun resetError() = _state.update { it.copy(error = null) }

    companion object {
        data class State(
            val checkedForLogin: Boolean = false,
            val error: DomainError? = null,
        )
    }
}