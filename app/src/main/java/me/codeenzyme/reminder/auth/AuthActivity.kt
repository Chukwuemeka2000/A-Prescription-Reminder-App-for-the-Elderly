package me.codeenzyme.reminder.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.codeenzyme.reminder.MainActivity
import me.codeenzyme.reminder.auth.ui.theme.ReminderTheme

class AuthActivity : ComponentActivity() {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReminderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //Greeting2("Android")
                    AuthScreen(::signUp, ::login)
                }
            }
        }
    }

    private fun signUp(firstName: String, lastName: String, email: String, password: String, phone: String) {
        viewModel.signUp(firstName, lastName, email, password, phone) {
            when (it) {
                AuthStatus.LoginFailure -> {}
                AuthStatus.LoginSuccess -> {}
                AuthStatus.SignUpFailure -> {
                    Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
                AuthStatus.SignUpSuccess -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password) {
            when (it) {
                AuthStatus.LoginFailure -> Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT)
                    .show()
                AuthStatus.LoginSuccess -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                AuthStatus.SignUpFailure -> {}
                AuthStatus.SignUpSuccess -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreen(signUp: (String, String, String, String, String) -> Unit, login: (email: String, password: String) -> Unit) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = AuthScreen.Login.route) {
        composable(route = AuthScreen.Login.route) {
            LoginScreen(login) {
                navController.navigate(AuthScreen.SignUp.route)
            }
        }
        composable(route = AuthScreen.SignUp.route) {
            SignupScreen(signUp) {
                navController.navigate(AuthScreen.Login.route)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreen(login: (email: String, password: String) -> Unit, goToSignUp: () -> Unit = {}) {

    val focusManager = LocalFocusManager.current

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var signInEnabled by remember {
        mutableStateOf(true)
    }

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Gray)) {
            append("Don't have an account? ")
        }

        withStyle(style = SpanStyle(color = Color.Blue)) {
            append("Sign up")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Welcome, Login", fontSize = 30.sp)
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = email, onValueChange = { email = it }, label = { Text("Email") }, isError = email.isBlank())
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = password, onValueChange = { password = it }, label = { Text("Password") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }), visualTransformation = PasswordVisualTransformation(), isError = password.length < 6)
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                focusManager.clearFocus(true)
                signInEnabled = false
                login(email, password)
            }) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            ClickableText(text = annotatedString, onClick = {goToSignUp()})
        }

        if (!signInEnabled) {
            CircularProgressIndicator(modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreen(signUp: (String, String, String, String, String) -> Unit, goToLogin: () -> Unit = {}) {

    val focusManager = LocalFocusManager.current

    var firstName by remember {
        mutableStateOf("")
    }

    var lastName by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var signUpEnabled by remember {
        mutableStateOf(true)
    }

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Gray)) {
            append("Already have an account? ")
        }

        withStyle(style = SpanStyle(color = Color.Blue)) {
            append("Login")
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Sign Up", fontSize = 30.sp)
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = firstName, onValueChange = { firstName = it }, label = { Text("First name") }, isError = firstName.isBlank())
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = lastName, onValueChange = { lastName = it }, label = { Text("Last name") }, isError = lastName.isBlank())
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), isError = phone.isBlank())
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = email, onValueChange = { email = it }, label = { Text("Email") }, isError = email.isBlank())
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = password, onValueChange = { password = it }, label = { Text("Password") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }), visualTransformation = PasswordVisualTransformation(), isError = password.length < 6)
            Button(modifier = Modifier.fillMaxWidth(), enabled = signUpEnabled, onClick = {
                focusManager.clearFocus(true)
                signUpEnabled = false
                signUp(firstName, lastName, email, password, phone)
            }) {
                Text("Create Account")
            }
            Spacer(modifier = Modifier.height(16.dp))
            ClickableText(text = annotatedString, onClick = {goToLogin()})
        }

        if (!signUpEnabled) {
            CircularProgressIndicator(modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center))
        }
    }
}

@Composable
fun Greeting2(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    ReminderTheme {
        Greeting2("Android")
    }
}