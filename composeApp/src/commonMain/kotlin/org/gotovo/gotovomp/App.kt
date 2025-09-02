package org.gotovo.gotovomp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import org.gotovo.gotovomp.domain.GlobalEvent
import org.gotovo.gotovomp.domain.UI
import org.gotovo.gotovomp.presentation.screens.AuthScreen
import org.gotovo.gotovomp.presentation.screens.CreateRecipeScreen
import org.gotovo.gotovomp.presentation.screens.FeedScreen
import org.gotovo.gotovomp.presentation.screens.PersonalScreen
import org.gotovo.gotovomp.presentation.screens.ProfileScreen
import org.gotovo.gotovomp.presentation.screens.ViewRecipeScreen
import org.gotovo.gotovomp.presentation.util.AppThemeConfiguration
import org.gotovo.gotovomp.presentation.util.Nav
import org.gotovo.gotovomp.presentation.util.appDarkScheme
import org.gotovo.gotovomp.presentation.util.appLightScheme
import org.gotovo.gotovomp.presentation.util.makeTypography
import org.gotovo.gotovomp.viewModels.PersonalVM

@OptIn(ExperimentalSharedTransitionApi::class)
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
                val personalVM: PersonalVM = koinViewModel()
                SharedTransitionLayout {
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navCtrl,
                        startDestination = Nav.FEED.route,
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                    ) {
                        composable(Nav.FEED.route) { FeedScreen(navCtrl, animatedScope = this@composable) }
                        composable(Nav.MINE.route) { PersonalScreen(navCtrl, viewModel = personalVM, animatedScope = this@composable) }
                        composable(Nav.PROFILE.route) { ProfileScreen() }
                        composable(Nav.VIEW.route + "/{id}") {
                            val recipeId = it.arguments?.getString("id")?.toLong()
                            if (recipeId != null) {
                                ViewRecipeScreen(recipeId, onBack = { navCtrl.navigateUp() }, modifier = Modifier.sharedBounds(
                                    rememberSharedContentState("view${recipeId}"),
                                    animatedVisibilityScope = this@composable
                                ))
                            }
                        }
                        composable(Nav.CREATE.route) { CreateRecipeScreen(onCreated = { navCtrl.navigateUp() }, viewModel = personalVM) }
                    }
                }
            }
        } else {
            Scaffold { AuthScreen() }
        }
    }
}
