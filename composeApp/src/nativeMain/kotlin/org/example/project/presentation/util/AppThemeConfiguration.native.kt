package org.example.project.presentation.util

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
actual fun AppThemeConfiguration(
    darkTheme: Boolean,
    lightColors: ColorScheme,
    darkColors: ColorScheme,
    typography: Typography,
    content: @Composable () -> Unit
) {
    val colorScheme = lightColors
    MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
}