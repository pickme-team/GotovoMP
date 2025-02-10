package org.example.project

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.example.project.domain.GlobalEvent
import org.example.project.domain.UI
import org.example.project.presentation.screens.AuthScreen
import org.example.project.presentation.screens.CreateRecipeScreen
import org.example.project.presentation.screens.FeedScreen
import org.example.project.presentation.screens.PersonalScreen
import org.example.project.presentation.screens.ProfileScreen
import org.example.project.presentation.screens.ViewRecipeScreen
import org.example.project.presentation.util.AppThemeConfiguration
import org.example.project.presentation.util.Nav
import org.example.project.presentation.util.appDarkScheme
import org.example.project.presentation.util.appLightScheme
import org.example.project.presentation.util.makeTypography
import org.example.project.viewModels.PersonalVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navCtrl = rememberNavController()
    var loggedIn by remember { mutableStateOf(false) }
    val lastEvent by UI.GlobalEventFlow.collectAsState(null)
    LaunchedEffect(lastEvent) {
        when (lastEvent) {
            GlobalEvent.Logout -> loggedIn = false
            GlobalEvent.Login -> loggedIn = true
            null -> Unit
        }
    }
    AppThemeConfiguration(
        darkTheme = isSystemInDarkTheme(),
        lightColors = appLightScheme,
        darkColors = appDarkScheme,
        typography = makeTypography(),
    ) {
        if (loggedIn) {
            val current =
                Nav.entries.find { navCtrl.currentBackStackEntryAsState().value?.destination?.route == it.route }
            Scaffold(bottomBar = {
                AnimatedContent(current?.icon != null) { showNavBar ->
                    if (showNavBar) BottomAppBar(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Nav.entries.forEach {
                            val selected = current == it
                            it.icon?.let { ic ->
                                NavigationBarItem(selected, icon = {
                                    Icon(
                                        if (selected) ic.selectedIcon else ic.unselectedIcon, null
                                    )
                                }, label = {
                                    Text(
                                        it.title, style = MaterialTheme.typography.labelLarge
                                    )
                                }, alwaysShowLabel = false, onClick = {
                                    navCtrl.navigate(it.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(
                                            navCtrl.graph.startDestinationRoute ?: return@navigate
                                        ) {
                                            saveState = true
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }) { innerPadding ->
                val personalVM: PersonalVM = viewModel()
                NavHost(
                    modifier = Modifier.padding(innerPadding),
                    navController = navCtrl,
                    startDestination = Nav.FEED.route
                ) {
                    composable(Nav.FEED.route) { FeedScreen(navCtrl) }
                    composable(Nav.MINE.route) { PersonalScreen(navCtrl, viewModel = personalVM) }
                    composable(Nav.PROFILE.route) { ProfileScreen() }
                    composable(Nav.VIEW.route + "/{id}") {
                        val recipeId = it.arguments?.getString("id")?.toLong()
                        if (recipeId != null) {
                            ViewRecipeScreen(recipeId, onBack = { navCtrl.navigateUp() })
                        }
                    }
                    composable(Nav.CREATE.route) { CreateRecipeScreen(onCreated = { navCtrl.navigateUp() }, viewModel = personalVM) }
                }
            }
        } else {
            Scaffold { AuthScreen() }
        }
    }
}
