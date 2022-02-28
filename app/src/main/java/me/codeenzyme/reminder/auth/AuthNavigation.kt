package me.codeenzyme.reminder.auth

// Used for managing navigation routes
sealed class AuthScreen(val route: String) {
    object Login: AuthScreen("login")
    object SignUp: AuthScreen("signup")
}