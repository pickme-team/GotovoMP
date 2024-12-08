package org.example.project.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Nav(val route: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    MAIN("main", Icons.Filled.Home, Icons.Outlined.Home),
    AUTH("auth", Icons.Filled.Person, Icons.Outlined.Person)
}
