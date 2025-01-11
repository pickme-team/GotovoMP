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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.presentation.components.PhoneField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(logIn: () -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize().padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
        Text("GovnoMP", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.size(128.dp))
      Text("Введите номер телефона", style = MaterialTheme.typography.bodyLarge)
        PhoneField(
            onContinue = { phone ->
              println(phone)
              true
            })
        // Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = logIn,
            colors =
                ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.primary)) {
              Text("Продолжить") // TODO res
            }
      Spacer(modifier = Modifier.size(128.dp))
      Spacer(modifier = Modifier.size(64.dp))
      }
}
