package org.example.project

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.example.project.network.ApiClient
import org.example.project.network.createHttpClient
import org.example.project.presentation.screens.AuthScreen
import org.example.project.presentation.screens.FeedScreen
import org.example.project.presentation.screens.PersonalScreen
import org.example.project.presentation.screens.ProfileScreen
import org.example.project.presentation.util.AppThemeConfiguration
import org.example.project.presentation.util.Nav
import org.example.project.presentation.util.appDarkScheme
import org.example.project.presentation.util.appLightScheme
import org.example.project.presentation.util.makeTypography
import org.example.project.viewModels.FeedScreenVM
import org.example.project.viewModels.MainScreenVMState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

  val navCtrl = rememberNavController()
  val currentRoute = navCtrl.currentBackStackEntryAsState().value?.destination?.route
  var loggedIn by remember { mutableStateOf(false) }
  AppThemeConfiguration(
      darkTheme = isSystemInDarkTheme(),
      lightColors = appLightScheme,
      darkColors = appDarkScheme,
      typography = makeTypography(),
  ) {
    if (loggedIn) {
      val current = navCtrl.currentBackStackEntryAsState().value?.destination?.route
      Scaffold(
          bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
              Nav.entries.forEach {
                val selected = current == it.route
                NavigationBarItem(
                    selected,
                    icon = { Icon(if (selected) it.selectedIcon else it.unselectedIcon, null) },
                    label = { Text(it.title, style = MaterialTheme.typography.labelLarge) },
                    alwaysShowLabel = false,
                    onClick = { navCtrl.navigate(it.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(
                            navCtrl.graph.startDestinationRoute ?: return@navigate
                        ) {
                            saveState = true
                        }
                    } })
              }
            }
          }) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navCtrl,
                startDestination = Nav.FEED.route) {
                  composable(Nav.FEED.route) { FeedScreen() }
                  composable(Nav.MINE.route) { PersonalScreen() }
                  composable(Nav.PROFILE.route) { ProfileScreen() }
                }
          }
    } else {
      Scaffold { AuthScreen { loggedIn = true } }
    }
  }
}
