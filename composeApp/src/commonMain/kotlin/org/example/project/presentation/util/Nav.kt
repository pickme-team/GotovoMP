package org.example.project.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class Nav(val route: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val title: String) {
    MINE("mine", Icons.Filled.Star, Icons.Outlined.Star, "Мои Рецеты"),
    FEED("main", Icons.Filled.Home, Icons.Outlined.Home, "Лента"),
    PROFILE("profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, "Профиль")
}
