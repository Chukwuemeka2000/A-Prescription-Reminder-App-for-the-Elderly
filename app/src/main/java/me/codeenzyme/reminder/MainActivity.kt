package me.codeenzyme.reminder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.codeenzyme.reminder.auth.AuthActivity
import me.codeenzyme.reminder.ui.theme.ReminderTheme

class MainActivity : ComponentActivity() {

    // called by the Android system when instantiating the activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // if the user is not logged in, move to the Auth activity
        if (Firebase.auth.currentUser == null) {
            startActivity(
                Intent(this, AuthActivity::class.java),
            )
            finish()
        }

        /*
        * Adding an auth state listener to listen to sign out and sign in events
        * */
        Firebase.auth.addAuthStateListener {
            // current user is null, if no user is logged in
            if (it.currentUser == null) {

                // sending an intent to the Android system to start the Auth activity
                startActivity(
                    Intent(this, AuthActivity::class.java),
                )
                finish() // finishing this activity to prevent navigating back
            }
        }


        // setting the UI of our activity using our app's composable theme
        setContent {
            ReminderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), // modifier for filling the full screen
                    color = MaterialTheme.colors.background // setting surface colors from app theme
                ) {
                    Greeting(getString(R.string.app_name))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    // Used to layout composable UI in vertical order
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Hello $name!")
        Button(onClick = { Firebase.auth.signOut() }) {
            Text(text = "Sign out")
        }
    }
}

@Preview(showBackground = true) // to preview composable UI in android studio
@Composable // Annotation to convert a function to a composable
fun DefaultPreview() {
    ReminderTheme {
        Greeting("Android")
    }
}