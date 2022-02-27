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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Firebase.auth.currentUser == null) {

        }

        Firebase.auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(
                    Intent(this, AuthActivity::class.java),
                )
            }
        }

        setContent {
            ReminderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(getString(R.string.app_name))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Hello $name!")
        Button(onClick = { Firebase.auth.signOut() }) {
            Text(text = "Sign out")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ReminderTheme {
        Greeting("Android")
    }
}