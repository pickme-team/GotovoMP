package org.example.project.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.domain.DomainError
import org.example.project.presentation.components.PhoneVisualTransformation
import org.example.project.presentation.components.TextLine
import org.example.project.viewModels.AuthVM
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction


@Composable
fun AuthScreen(authVM: AuthVM = viewModel()) {
    LaunchedEffect(Unit) {
        authVM.checkSavedLogin()
    }
    val (checkedToken, error) = authVM.state.collectAsState().value
    var register by remember { mutableStateOf(false) }
    if (checkedToken) {
        if (register) {
            Register(error, { register = false; authVM.resetError() })
        } else {
            LoginWithPhone(error, { register = true; authVM.resetError() })
        }
    } else {
        Surface() { CircularProgressIndicator() }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AuthColumn(
    topText: String,
    left: Pair<String, () -> Unit>,
    right: Pair<String, () -> Unit>,
    error: DomainError?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp).windowInsetsPadding(WindowInsets.ime),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(topText, style = MaterialTheme.typography.headlineSmall)
            Text("GovnoMP", style = MaterialTheme.typography.displayMedium)
        }
        content()
        error?.let {
            when (it) {
                DomainError.NetworkServerError.CONFLICT -> Text("User already exists")
                is DomainError.NetworkServerError -> Text("Server Error")
                is DomainError.NetworkClientError -> Text("Client Error")
                DomainError.Unknown -> Text("Unknown error")
            }
        }
        FlowRow(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth(), ) {
            OutlinedButton(onClick = left.second, shape = MaterialTheme.shapes.medium) {
                Text(left.first) // TODO res
            }
            Button(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1f),
                onClick = right.second,
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(right.first) // TODO res
            }
        }
    }
}

@Composable
private fun LoginWithPhone(
    error: DomainError?,
    toRegister: () -> Unit,
    authVM: AuthVM = viewModel(),
) {
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    val mask = "000 000 00 00"
    val maxLen = mask.count { it != ' ' }
    val maskNum = '0'
    val prefix = "+7"
    val action = { authVM.tryLogin(prefix + phoneNumber.text, password.text) }
    val fm = LocalFocusManager.current
    var lastFocused by remember { mutableStateOf(false) }
    val kbdActions = KeyboardActions {
        if (!lastFocused) fm.moveFocus(FocusDirection.Down) else action()
    }
    AuthColumn(
        topText = "Вход в",
        left = "Регистрация" to toRegister,
        right = "Продолжить" to action,
        error = error,
    ) {
        TextLine(
            phoneNumber,
            keyboardActions = kbdActions,
            onValueChange = { if (it.text.length <= maxLen) phoneNumber = it },
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            prefix = "$prefix ",
            placeholderText = mask,
            modifier = Modifier.fillMaxWidth()
        )
        TextLine(
            password,
            keyboardActions = kbdActions,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            placeholderText = "Пароль",
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                lastFocused = it.isFocused
            }
        )
    }
}

@Composable
private fun Register(
    error: DomainError?,
    toLogin: () -> Unit,
    authVM: AuthVM = viewModel(),
) {
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var username by remember { mutableStateOf(TextFieldValue()) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    val action = { authVM.tryRegister(firstName.text, lastName.text, username.text, phoneNumber.text, password.text) }
    var lastFocused by remember { mutableStateOf(false) }
    val fm = LocalFocusManager.current
    val kbdActions = KeyboardActions {
        if (!lastFocused) fm.moveFocus(FocusDirection.Down) else action()
    }
    LaunchedEffect(Triple(firstName, lastName, username), Pair(phoneNumber, password)) {
        authVM.resetError()
    }
    AuthColumn(
        topText = "Регистрация в",
        left = "Вход" to toLogin,
        right = "Продолжить" to action,
        error = error
    ) {
        TextLine(
            firstName,
            keyboardActions = kbdActions,
            onValueChange = { firstName = it },
            placeholderText = "Имя",
            modifier = Modifier.fillMaxWidth()
        )
        TextLine(
            lastName,
            keyboardActions = kbdActions,
            onValueChange = { lastName = it },
            placeholderText = "Фамилия",
            modifier = Modifier.fillMaxWidth()
        )
        TextLine(
            username,
            keyboardActions = kbdActions,
            onValueChange = { username = it },
            placeholderText = "Логин",
            modifier = Modifier.fillMaxWidth()
        )
        val mask = "000 000 00 00"
        val maxLen = mask.count { it != ' ' }
        val maskNum = '0'
        val prefix = "+7"
        TextLine(
            phoneNumber,
            onValueChange = { if (it.text.length <= maxLen) phoneNumber = it },
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            keyboardActions = kbdActions,
            prefix = "$prefix ",
            placeholderText = mask,
            modifier = Modifier.fillMaxWidth()
        )
        TextLine(
            password,
            onValueChange = { password = it },
            keyboardActions = kbdActions,
            visualTransformation = PasswordVisualTransformation(),
            placeholderText = "Пароль",
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                lastFocused = it.isFocused
            }
        )
    }
}
