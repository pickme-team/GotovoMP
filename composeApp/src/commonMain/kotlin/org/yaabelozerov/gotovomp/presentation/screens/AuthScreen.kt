package org.yaabelozerov.gotovomp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import org.yaabelozerov.gotovomp.domain.DomainError
import org.yaabelozerov.gotovomp.presentation.components.PhoneVisualTransformation
import org.yaabelozerov.gotovomp.presentation.components.TextLine
import org.yaabelozerov.gotovomp.viewModels.AuthVM
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.util.fastAll
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.yaabelozerov.gotovomp.Setter
import org.yaabelozerov.gotovomp.invoke
import org.yaabelozerov.gotovomp.presentation.util.ValidationState
import org.yaabelozerov.gotovomp.presentation.util.validation
import org.yaabelozerov.gotovomp.presentation.util.isError
import org.yaabelozerov.gotovomp.presentation.util.isValid
import org.yaabelozerov.gotovomp.setter


@Composable
fun AuthScreen(authVM: AuthVM = koinViewModel()) {
    LaunchedEffect(Unit) {
        authVM.checkSavedLogin()
    }
    val (checkedToken, error) = authVM.state.collectAsState().value
    var phone by remember { mutableStateOf(TextFieldValue()) }
    val setPhone = phone.setter { phone = it }
    var password by remember { mutableStateOf(TextFieldValue()) }
    val setPassword = password.setter { password = it }
    val pager = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val scroll = { it: Int -> scope.launch {
        pager.animateScrollToPage(it)
    } }
    if (checkedToken) {
        HorizontalPager(pager) { page ->
            when (page) {
                0 -> LoginWithPhone(setPhone, setPassword, error, { scroll(1); authVM.resetError() })
                1 -> Register(setPhone, setPassword, error, { scroll(0); authVM.resetError() })
            }
        }
    } else {
        Surface() { CircularProgressIndicator() }
    }
}

@Composable
private fun LoginWithPhone(
    phone: Setter<TextFieldValue>,
    password: Setter<TextFieldValue>,
    error: DomainError?,
    toRegister: () -> Unit,
    authVM: AuthVM = koinViewModel(),
) {
    val mask = "000 000 00 00"
    val maxLen = mask.count { it != ' ' }
    val maskNum = '0'
    val prefix = "+7"
    val action = { authVM.tryLogin(prefix + phone(), password()) }
    val fm = LocalFocusManager.current
    var lastFocused by remember { mutableStateOf(false) }

    val phoneValidation by derivedStateOf {
        when {
            phone().isEmpty() -> ValidationState.Unset
            phone().run { length < maxLen && all { it.isDigit() } } -> ValidationState.Invalid
            else -> ValidationState.Valid
        }
    }
    val passwordValidation by derivedStateOf {
        when {
            password().isEmpty() -> ValidationState.Unset
            else -> ValidationState.Valid
        }
    }

    val authAvailable by derivedStateOf {
        listOf(phoneValidation, passwordValidation).fastAll { it.isValid() }
    }

    val kbdActions = KeyboardActions {
        if (!lastFocused) fm.moveFocus(FocusDirection.Down) else { if (authAvailable) action() }
    }

    LaunchedEffect(Pair(phone(), password())) {
        authVM.resetError()
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).windowInsetsPadding(WindowInsets.ime),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text("Вход", style = MaterialTheme.typography.headlineSmall)
            Text("GotovoMP", style = MaterialTheme.typography.displayMedium)
        }
        var phoneFocused by remember { mutableStateOf(false) }
        TextLine(
            phone.value,
            keyboardActions = kbdActions,
            onValueChange = {
                if (it.text.length <= maxLen && it.text.all { it.isDigit() }) phone(it)
            },
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            prefix = "$prefix ",
            placeholderText = mask,
            modifier = Modifier.fillMaxWidth().onFocusChanged { phoneFocused = it.isFocused },
            isError = phoneValidation.isError() && !phoneFocused,
            errorText = "Неправильный номер телефона"
        )
        TextLine(
            password.value,
            keyboardActions = kbdActions,
            onValueChange = { password(it) },
            visualTransformation = PasswordVisualTransformation(),
            placeholderText = "Пароль",
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                lastFocused = it.isFocused
            }
        )
        error?.let {
            when (it) {
                is DomainError.NetworkClientError.NoInternet -> "No internet connection."
                is DomainError.NetworkClientError.Serialization -> "Error retrieving data."
                is DomainError.NetworkServerError.ServerError -> "Server error. Please try again later."
                is DomainError.NetworkServerError.Unauthorized -> "Wrong combination of login and password."
                is DomainError.NetworkServerError.NotFound, is DomainError.NetworkServerError.Conflict -> { toRegister(); null }
                is DomainError.Unknown -> "Unknown error. Please try again later."
            }?.let { errorMessage ->
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
        FlowRow(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth(), ) {
            OutlinedButton(onClick = toRegister, shape = MaterialTheme.shapes.medium) {
                Text("Регистрация")
            }
            Button(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1f),
                onClick = action,
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.primary),
                enabled = authAvailable
            ) {
                Text("Войти")
            }
        }
    }
}

@Composable
private fun Register(
    phone: Setter<TextFieldValue>,
    password: Setter<TextFieldValue>,
    error: DomainError?,
    toLogin: () -> Unit,
    authVM: AuthVM = koinViewModel(),
) {
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var username by remember { mutableStateOf(TextFieldValue()) }
    val phonePrefix = "+7"
    val action = { authVM.tryRegister(firstName.text, lastName.text, username.text, phonePrefix + phone(), password()) }
    var lastFocused by remember { mutableStateOf(false) }
    val fm = LocalFocusManager.current

    val firstNameValid by derivedStateOf { firstName.text.validation { it.length in 3..25 && it.all(Char::isLetter) } }
    val lastNameValid by derivedStateOf { lastName.text.validation { it.length in 3..25 && it.all(Char::isLetter) } }
    val userNameValid by derivedStateOf { username.text.validation { it.length in 5..30 && it.all { it.isLetter() || it == '_' || it.isDigit() } }}
    val phoneValid by derivedStateOf { phone().validation { it.length == 10 && it.all { it.isDigit() }}}
    val passwordValid by derivedStateOf { password().validation { it.length in 8..30 } }

    val authAvailable by derivedStateOf {
        listOf(firstNameValid, lastNameValid, userNameValid, phoneValid, passwordValid).fastAll { it.isValid() }
    }

    val kbdActions = KeyboardActions {
        if (!lastFocused) fm.moveFocus(FocusDirection.Down) else { if (authAvailable) action() }
    }

    LaunchedEffect(Triple(firstName, lastName, username), Pair(phone(), password())) {
        authVM.resetError()
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).windowInsetsPadding(WindowInsets.ime),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text("Регистрация", style = MaterialTheme.typography.headlineSmall)
            Text("GotovoMP", style = MaterialTheme.typography.displayMedium)
        }
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
        var phoneFocused by remember { mutableStateOf(false) }
        TextLine(
            phone.value,
            onValueChange = { if (it.text.length <= maxLen && it.text.all { it.isDigit() }) phone(it) },
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            keyboardActions = kbdActions,
            prefix = "$phonePrefix ",
            placeholderText = mask,
            modifier = Modifier.fillMaxWidth().onFocusChanged { phoneFocused = it.isFocused },
            isError = phoneValid.isError() && !phoneFocused,
            errorText = "Неправильный номер телефона"
        )
        var passwordFocused by remember { mutableStateOf(false) }
        TextLine(
            password.value,
            onValueChange = { password(it) },
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
        error?.let {
            when (it) {
                is DomainError.NetworkClientError.NoInternet -> "No internet connection."
                is DomainError.NetworkClientError.Serialization -> "Error retrieving data."
                is DomainError.NetworkServerError.ServerError -> "Server error. Please try again later."
                is DomainError.NetworkServerError.Unauthorized -> "Wrong combination of login and password."
                is DomainError.NetworkServerError.NotFound, is DomainError.NetworkServerError.Conflict -> { toLogin(); null }
                is DomainError.Unknown -> "Unknown error. Please try again later."
            }?.let { errorMessage ->
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
        FlowRow(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth(), ) {
            OutlinedButton(onClick = toLogin, shape = MaterialTheme.shapes.medium) {
                Text("Вход")
            }
            Button(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1f),
                onClick = action,
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.primary),
                enabled = authAvailable
            ) {
                Text("Зарегистрироваться")
            }
        }
    }
}
