package me.codeenzyme.reminder.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthRepository {

    // Signing up user with their data
    fun signUp(firstName: String, lastName: String, email: String, password: String, phone: String, onCompletion: (AuthStatus) -> Unit) {
        //Firstly, create user with email and password
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            // if successful update display name of the user and save profile data to DB
            if (task.isSuccessful) {
                val newUser = Firebase.auth.currentUser
                newUser?.let {
                    it.updateProfile(UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build())

                    // saving profile data to DB
                    Firebase.firestore.collection("users").document(it.uid).set(UserDataModel(firstName, lastName, phone)).addOnCompleteListener { userDataTask ->
                        // If successful sign up process is complete
                        if (userDataTask.isSuccessful) {
                            onCompletion(AuthStatus.SignUpSuccess)
                        }
                    }
                }
            } else {
                onCompletion(AuthStatus.SignUpFailure)
            }
        }
    }

    // Sign in a user using firebase and calls "onCompletion" with auth status
    fun login(email: String, password: String, onCompletion: (AuthStatus) -> Unit) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onCompletion(AuthStatus.LoginSuccess)
            } else {
                onCompletion(AuthStatus.LoginFailure)
            }
        }
    }

    // Used for signing out a user
    fun signOut() {
        Firebase.auth.signOut()
    }

}