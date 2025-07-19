package org.yaabelozerov.gotovomp.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class NavIcon(val selectedIcon: ImageVector, val unselectedIcon: ImageVector)

enum class Nav(val route: String, val icon: NavIcon?, val title: String) {
    MINE("mine", NavIcon(Icons.Filled.Star, Icons.TwoTone.Star), "Мои Рецепты"),
    FEED("main", NavIcon(Icons.Filled.Home, Icons.TwoTone.Home), "Лента"),
    PROFILE("profile", NavIcon(Icons.Filled.AccountCircle, Icons.TwoTone.AccountCircle), "Профиль"),
    CREATE("create", null, "Создать рецепт"),
    VIEW("view", null, "Просмотр рецепта"),
}
