package org.example.project.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager


@Composable
fun AuthScreen(authVM: AuthVM = viewModel()) {
    LaunchedEffect(Unit) {
        authVM.checkSavedLogin()
    }
    val (checkedToken, error) = authVM.state.collectAsState().value
    var register by remember { mutableStateOf(false) }
    if (checkedToken) {
        if (register) {
            Register(error, { register = false; authVM.resetError() }, authAvailable = mutableStateOf(false))
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
    authAvailable: MutableState<Boolean>,
    content: @Composable () -> Unit,

) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp).windowInsetsPadding(WindowInsets.ime),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(topText, style = MaterialTheme.typography.headlineSmall)
            Text("GotovoMP", style = MaterialTheme.typography.displayMedium)
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
                    .copy(containerColor = MaterialTheme.colorScheme.primary),
                enabled = authAvailable.value
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
        authAvailable = mutableStateOf(true)
    ) {
        var isPhoneNumberCorrect = remember (phoneNumber) {phoneNumber.text.length == 10 && phoneNumber.text.all { it.isDigit() }}
        var isPhoneNumberTyped by remember { mutableStateOf(false) }
        TextLine(
            phoneNumber,
            keyboardActions = kbdActions,
            onValueChange = {
                if (it.text.length <= maxLen) phoneNumber = it
                isPhoneNumberTyped = true
                println("${!isPhoneNumberTyped} $isPhoneNumberCorrect")
                println(phoneNumber.text.length)
                println(phoneNumber.text.all { num -> num.isDigit() })
                            },
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            prefix = "$prefix ",
            placeholderText = mask,
            modifier = Modifier.fillMaxWidth(),
            isError = mutableStateOf(!isPhoneNumberCorrect && isPhoneNumberTyped),
            errorText = "Неправильный номер телефона"
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
    authAvailable: MutableState<Boolean>
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

    val isFirstNameCorrect = remember (firstName) {firstName.text.length in 3..25 && firstName.text.all { it.isLetter()}}
    val isLastNameCorrect = remember (lastName) {lastName.text.length in 3..25 && lastName.text.all { it.isLetter() }}
    val isUsernameCorrect = remember (username) {username.text.length in 5..30 && username.text.all { it.isLetter() || it == '_' }}
    val isPhoneNumberCorrect = remember (phoneNumber) {phoneNumber.text.length == 10 && phoneNumber.text.all { it.isDigit() }}
    val isPasswordCorrect = remember (password) {password.text.length in 8..30 }

    fun checkAuth() {
        authAvailable.value = isFirstNameCorrect && isLastNameCorrect && isUsernameCorrect && isPhoneNumberCorrect && isPasswordCorrect
    }


    LaunchedEffect(Triple(firstName, lastName, username), Pair(phoneNumber, password)) {
        authVM.resetError()
    }
    AuthColumn(
        topText = "Регистрация в",
        left = "Вход" to toLogin,
        right = "Продолжить" to action,
        error = error,
        authAvailable = authAvailable
    ) {
        var firstNameTyped by remember { mutableStateOf(false) }
        TextLine(
            firstName,
            keyboardActions = kbdActions,
            onValueChange = { firstName = it
                            firstNameTyped = true
                            checkAuth()},
            placeholderText = "Имя",
            modifier = Modifier.fillMaxWidth(),
            isError = mutableStateOf(!isFirstNameCorrect && firstNameTyped),
            errorText = "Имя должно быть от 3 до 25 символов длинной и содержать только буквы"
        )
        var lastNameTyped by remember { mutableStateOf(false) }
        TextLine(
            lastName,
            keyboardActions = kbdActions,
            onValueChange = { lastName = it
                            lastNameTyped = true
                checkAuth()},
            placeholderText = "Фамилия",
            modifier = Modifier.fillMaxWidth(),
            isError = mutableStateOf(!isLastNameCorrect && lastNameTyped),
            errorText = "Фамилия должна быть от 3 до 25 символов длинной и содержать только буквы"
        )
        var usernameTyped by remember { mutableStateOf(false) }
        TextLine(
            username,
            keyboardActions = kbdActions,
            onValueChange = { username = it
                            usernameTyped = true
                checkAuth()},
            placeholderText = "Логин",
            modifier = Modifier.fillMaxWidth(),
            isError = mutableStateOf(!isUsernameCorrect && usernameTyped),
            errorText = "Логин должен быть от 5 до 30 символов длинной и содержать только буквы и _"


        )
        val mask = "000 000 00 00"
        val maxLen = mask.count { it != ' ' }
        val maskNum = '0'
        val prefix = "+7"
        var isPhoneNumberTyped by remember { mutableStateOf(false) }
        TextLine(
            phoneNumber,
            onValueChange = { if (it.text.length <= maxLen) phoneNumber = it
                isPhoneNumberTyped = true
                checkAuth()},
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            keyboardActions = kbdActions,
            prefix = "$prefix ",
            placeholderText = mask,
            modifier = Modifier.fillMaxWidth(),
            isError = mutableStateOf(!isPhoneNumberCorrect && isPhoneNumberTyped),
            errorText = "Неправильный номер телефона"

        )
        var passwordTyped by remember { mutableStateOf(false) }
        TextLine(
            password,
            onValueChange = { password = it
                            passwordTyped = true
                checkAuth()},
            keyboardActions = kbdActions,
            visualTransformation = PasswordVisualTransformation(),
            placeholderText = "Пароль",
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                lastFocused = it.isFocused
            },
            isError = mutableStateOf(!isPasswordCorrect && passwordTyped),
            errorText = "Пароль должен быть от 8 до 30 символов длинной"
        )
        authAvailable.value = isFirstNameCorrect && isLastNameCorrect && isUsernameCorrect && isPhoneNumberCorrect && isPasswordCorrect

    }
}
