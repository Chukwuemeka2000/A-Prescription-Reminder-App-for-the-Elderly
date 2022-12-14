package me.codeenzyme.reminder

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.codeenzyme.reminder.auth.AuthActivity
import me.codeenzyme.reminder.home.HomeScreen
import me.codeenzyme.reminder.profile.ProfileScreen
import me.codeenzyme.reminder.ui.theme.ReminderTheme

val Context.dataPreferences: DataStore<Preferences> by preferencesDataStore(name = "Settings")
val DEFAULT_RINGTONE_PREFERENCE_KEY = "DEFAULT_RINGTONE_PREFERENCE_KEY"

class MainActivity : ComponentActivity() {
    private val SETTINGS_ACTION_ID = 0xf343ab

    private var appBar: ActionBar? = null

    val ringtoneLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val dataIntent = it.data
            val dataUri: Uri? = dataIntent?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//                dataIntent?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
//            }else{
//                dataIntent?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
//            }
            lifecycleScope.launch(context = Dispatchers.IO) {
                dataUri?.let {itUri -> dataPreferences.edit { settings -> settings[stringPreferencesKey(DEFAULT_RINGTONE_PREFERENCE_KEY)] = itUri.toString() }
                    withContext(Dispatchers.Main){
                        Toast.makeText(baseContext, "Saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // called by the Android system when instantiating the activity.
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appBar = actionBar
        appBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF3700B3")))

        lifecycleScope.launch(context = Dispatchers.IO) {
            dataPreferences.data.first()
        }

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
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(), // modifier for filling the full screen
                    color = MaterialTheme.colors.background // setting surface colors from app theme
                ) {
                    //Greeting(getString(R.string.app_name))
                    Scaffold(bottomBar = {
                        BottomNav(navController)
                    }) {
                        AppScreens(navController)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            if(!powerManager.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)){
                startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).let {
                    it.data = Uri.parse("package:$packageName")
                    it
                });
                /*androidx.appcompat.app.AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog).setTitle("Reminder would need to exit battery optimization to work properly. Please permit this request.")
                    .setPositiveButton("Ok"){dialog, action ->
                        dialog.cancel()
//                        startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) would open settings
                        startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS));
                    }
                    .setNegativeButton("Cancel"){dialog, action -> dialog.cancel() }
//                    .create()
                    .show()*/
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menu?.add(0, SETTINGS_ACTION_ID, 0, "Select Tone")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when(item.itemId){
            SETTINGS_ACTION_ID -> {
                val defaultToneString = runBlocking {
                    dataPreferences.data.map {settings ->
                        settings[stringPreferencesKey(DEFAULT_RINGTONE_PREFERENCE_KEY)] ?: ""
                    }.first()
                }
                val defaultToneUri = if (defaultToneString.isBlank()){
                    RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
                }else{
                    Uri.parse(defaultToneString)
                }
                ringtoneLauncher.launch(
                    Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, defaultToneUri)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                    }
                )
                true
            }
            else -> false
        }
    }
}

@Composable
fun AppScreens(navController: NavController) {

    NavHost(navController = navController as NavHostController, startDestination = AppBottomNavigation.Home.route) {
        composable(route = AppBottomNavigation.Home.route) {
            HomeScreen()
        }
        composable(route = AppBottomNavigation.Profile.route) {
            ProfileScreen()
        }
    }
}

@Composable
fun BottomNav(navController: NavController) {
    val bottomNavItems = listOf(
        AppBottomNavigation.Home,
        AppBottomNavigation.Profile
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach {
            BottomNavigationItem(
                selected = currentRoute == it.route,
                onClick = { navController.navigate(it.route) },
                icon = {
                    Icon(imageVector = it.icon, contentDescription = it.label)
                },
                label = {
                    Text(it.label)
                }, alwaysShowLabel = false
            )
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