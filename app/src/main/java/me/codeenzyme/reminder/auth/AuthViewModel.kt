package me.codeenzyme.reminder.auth

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    // calls the repository to handle sign up process
    fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        onCompletion: (AuthStatus) -> Unit
    ) {
        repo.signUp(firstName, lastName, email, password, phone, onCompletion)
    }

    // call the auth repository to handle login process
    fun login(email: String, password: String, onCompletion: (AuthStatus) -> Unit) {
        repo.login(email, password, onCompletion)
    }

}