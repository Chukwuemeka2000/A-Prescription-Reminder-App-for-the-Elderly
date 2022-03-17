package me.codeenzyme.reminder

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppBottomNavigation(val route: String, val icon: ImageVector, val label: String) {

    object Home: AppBottomNavigation("home", Icons.Filled.Home, "Home")
    object Profile: AppBottomNavigation("profile", Icons.Filled.Person, "Profile")

}