package org.example.project.presentation.util

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
expect fun AppThemeConfiguration(
    darkTheme: Boolean,
    lightColors: ColorScheme,
    darkColors: ColorScheme,
    typography: Typography,
    content: @Composable () -> Unit
)