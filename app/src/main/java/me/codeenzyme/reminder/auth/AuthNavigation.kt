package me.codeenzyme.reminder.auth

sealed class AuthScreen(val route: String) {
    object Login: AuthScreen("login")
    object SignUp: AuthScreen("signup")
}