package org.example.project

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.presentation.screens.AuthScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
@Composable
fun App() {

  val navCtrl = rememberNavController()
  val currentRoute = navCtrl.currentBackStackEntryAsState().value?.destination?.route
  MaterialTheme { AuthScreen() }
}
