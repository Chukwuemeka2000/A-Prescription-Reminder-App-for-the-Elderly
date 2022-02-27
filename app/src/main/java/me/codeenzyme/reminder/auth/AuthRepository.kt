package me.codeenzyme.reminder.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthRepository {

    fun signUp(firstName: String, lastName: String, email: String, password: String, phone: String, onCompletion: (AuthStatus) -> Unit) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newUser = Firebase.auth.currentUser
                newUser?.let {
                    it.updateProfile(UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build())

                    Firebase.firestore.collection("users").document(it.uid).set(UserDataModel(firstName, lastName, phone)).addOnCompleteListener { userDataTask ->
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

    fun login(email: String, password: String, onCompletion: (AuthStatus) -> Unit) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onCompletion(AuthStatus.LoginSuccess)
            } else {
                onCompletion(AuthStatus.LoginFailure)
            }
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

}