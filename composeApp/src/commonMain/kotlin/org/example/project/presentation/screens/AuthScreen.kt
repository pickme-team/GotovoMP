package org.example.project.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNodeLifecycleCallback
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
import org.example.project.presentation.components.CustomTextField
import org.example.project.presentation.components.PhoneField
import org.example.project.presentation.components.PhoneVisualTransformation
import org.example.project.presentation.components.TextLine
import org.example.project.viewModels.AuthVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(callback: () -> Unit, authVM: AuthVM = viewModel()) {
    val error = authVM.state.collectAsState().value.error
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text("GovnoMP", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.size(128.dp))
        Text("Введите номер телефона", style = MaterialTheme.typography.bodyLarge)
        val mask = "000 000 00 00"
        val maxLen = mask.count { it != ' ' }
        val maskNum = '0'
        val prefix = "+7"
        TextLine(
            phoneNumber,
            onValueChange = { if (it.text.length <= maxLen) phoneNumber = it },
            visualTransformation = PhoneVisualTransformation(mask, maskNum),
            prefix = "$prefix "
        )
        TextLine(
            password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )
        // Spacer(modifier = Modifier.size(16.dp))
        error?.let {
            when (it) {
                DomainError.NetworkServerError.CONFLICT -> Text("User already exists")
                is DomainError.NetworkServerError -> Text("Server Error")
                is DomainError.NetworkClientError -> Text("Client Error")
                DomainError.Unknown -> Text("Unknown error")
            }
        }
        Button(
            onClick = { authVM.tryLogin(prefix + phoneNumber.text, password.text, callback) },
            colors = ButtonDefaults.buttonColors()
                .copy(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Продолжить") // TODO res
        }
        Spacer(modifier = Modifier.size(128.dp))
        Spacer(modifier = Modifier.size(64.dp))
    }
}
