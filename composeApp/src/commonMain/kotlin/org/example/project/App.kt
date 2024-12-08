package org.example.project

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.presentation.screens.AuthScreen

@Composable
fun App() {
  MaterialTheme { AuthScreen() }
}
