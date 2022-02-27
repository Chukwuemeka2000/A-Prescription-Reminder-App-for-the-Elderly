package me.codeenzyme.reminder.auth

sealed class AuthStatus {
    object SignUpSuccess: AuthStatus()
    object SignUpFailure: AuthStatus()
    object LoginSuccess: AuthStatus()
    object LoginFailure: AuthStatus()
}