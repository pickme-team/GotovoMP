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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.util.fastAll
import org.example.project.presentation.util.ValidationState
import org.example.project.presentation.util.validation
import org.example.project.presentation.util.isError
import org.example.project.presentation.util.isValid


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
    authAvailable: Boolean,
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
                enabled = authAvailable
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

    val phoneValidation by derivedStateOf {
        when {
            phoneNumber.text.isEmpty() -> ValidationState.Unset
            phoneNumber.text.run { length < maxLen && all { it.isDigit() } } -> ValidationState.Invalid
            else -> ValidationState.Valid
        }
    }
    val passwordValidation by derivedStateOf {
        when {
            password.text.isEmpty() -> ValidationState.Unset
            else -> ValidationState.Valid
        }
    }

    val authAvailable by derivedStateOf {
        listOf(phoneValidation, passwordValidation).fastAll { it.isValid() }
    }

    val kbdActions = KeyboardActions {
        if (!lastFocused) fm.moveFocus(FocusDirection.Down) else { if (authAvailable) action() }
    }

    LaunchedEffect(Pair(phoneNumber, password)) {
        authVM.resetError()
    }

    AuthColumn(
        topText = "Вход в",
        left = "Регистрация" to toRegister,
        right = "Продолжить" to action,
        error = error,
        authAvailable = authAvailable
    ) {
        var phoneFocused by remember { mutableStateOf(false) }
        TextLine(
            phoneNumber,
            keyboardActions = kbdActions,
            onValueChange = {
                if (it.text.length <= maxLen && it.text.all { it.isDigit() }) phoneNumber = it
            },
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            prefix = "$prefix ",
            placeholderText = mask,
            modifier = Modifier.fillMaxWidth().onFocusChanged { phoneFocused = it.isFocused },
            isError = phoneValidation.isError() && !phoneFocused,
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
) {
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var username by remember { mutableStateOf(TextFieldValue()) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    val action = { authVM.tryRegister(firstName.text, lastName.text, username.text, phoneNumber.text, password.text) }
    var lastFocused by remember { mutableStateOf(false) }
    val fm = LocalFocusManager.current

    val firstNameValid by derivedStateOf { firstName.text.validation { it.length in 3..25 && it.all(Char::isLetter) } }
    val lastNameValid by derivedStateOf { lastName.text.validation { it.length in 3..25 && it.all(Char::isLetter) } }
    val userNameValid by derivedStateOf { username.text.validation { it.length in 5..30 && it.all { it.isLetter() || it == '_' || it.isDigit() } }}
    val phoneValid by derivedStateOf { phoneNumber.text.validation { it.length == 10 && it.all { it.isDigit() }}}
    val passwordValid by derivedStateOf { password.text.validation { it.length in 8..30 } }

    val authAvailable by derivedStateOf {
        listOf(firstNameValid, lastNameValid, userNameValid, phoneValid, passwordValid).fastAll { it.isValid() }
    }

    val kbdActions = KeyboardActions {
        if (!lastFocused) fm.moveFocus(FocusDirection.Down) else { if (authAvailable) action() }
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
        var firstNameFocused by remember { mutableStateOf(false) }
        TextLine(
            firstName,
            keyboardActions = kbdActions,
            onValueChange = { firstName = it },
            placeholderText = "Имя",
            modifier = Modifier.fillMaxWidth().onFocusChanged { firstNameFocused = it.isFocused },
            isError = firstNameValid.isError() && !firstNameFocused,
            errorText = "Имя должно быть от 3 до 25 символов длинной и содержать только буквы"
        )
        var lastNameFocused by remember { mutableStateOf(false) }
        TextLine(
            lastName,
            keyboardActions = kbdActions,
            onValueChange = { lastName = it },
            placeholderText = "Фамилия",
            modifier = Modifier.fillMaxWidth().onFocusChanged { lastNameFocused = it.isFocused },
            isError = lastNameValid.isError() && !lastNameFocused,
            errorText = "Фамилия должна быть от 3 до 25 символов длинной и содержать только буквы"
        )
        var userNameFocused by remember { mutableStateOf(false) }
        TextLine(
            username,
            keyboardActions = kbdActions,
            onValueChange = { username = it },
            placeholderText = "Логин",
            modifier = Modifier.fillMaxWidth().onFocusChanged { userNameFocused = it.isFocused },
            isError = userNameValid.isError() && !userNameFocused,
            errorText = "Логин должен быть от 5 до 30 символов длинной и содержать только буквы, цифры и _"
        )
        val mask = "000 000 00 00"
        val maxLen = mask.count { it != ' ' }
        val maskNum = '0'
        val prefix = "+7"
        var phoneFocused by remember { mutableStateOf(false) }
        TextLine(
            phoneNumber,
            onValueChange = { if (it.text.length <= maxLen && it.text.all { it.isDigit() }) phoneNumber = it },
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            keyboardActions = kbdActions,
            prefix = "$prefix ",
            placeholderText = mask,
            modifier = Modifier.fillMaxWidth().onFocusChanged { phoneFocused = it.isFocused },
            isError = phoneValid.isError() && !phoneFocused,
            errorText = "Неправильный номер телефона"
        )
        var passwordFocused by remember { mutableStateOf(false) }
        TextLine(
            password,
            onValueChange = { password = it },
            keyboardActions = kbdActions,
            visualTransformation = PasswordVisualTransformation(),
            placeholderText = "Пароль",
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                lastFocused = it.isFocused
                passwordFocused = it.isFocused
            },
            isError = passwordValid.isError() && !passwordFocused,
            errorText = "Пароль должен быть от 8 до 30 символов длинной"
        )
    }
}
