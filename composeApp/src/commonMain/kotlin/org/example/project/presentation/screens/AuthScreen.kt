package org.example.project.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.presentation.components.PhoneField

@Composable
fun AuthScreen(
    logIn: () -> Unit
) {
  Column(
      modifier = Modifier.fillMaxSize().padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
  ) {
        PhoneField(
            onContinue = { phone ->
              println(phone)
              true
            })
        Button(
            onClick = logIn,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary
            )) {
          Text("Продолжить") // TODO res
        }
      }
}
