package me.codeenzyme.reminder.auth

// Used for tracking the Auth status when an auth operation is performed
sealed class AuthStatus {
    object SignUpSuccess: AuthStatus()
    object SignUpFailure: AuthStatus()
    object LoginSuccess: AuthStatus()
    object LoginFailure: AuthStatus()
}