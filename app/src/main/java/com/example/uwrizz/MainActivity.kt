@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uwrizz

// Import everything that's necessary
import UserDatabaseHelper
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.uwrizz.ui.theme.UWRizzTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers


// Replace this with your application's package name
private const val USER_PREFERENCES_NAME = "com.example.uwrizz"

// Just after the package declaration
//val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

// The dataStore by delegate
private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the login state to logged out
        val dataStore = applicationContext.dataStore
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { settings ->
                settings[booleanPreferencesKey("IS_LOGGED_IN")] = false
            }
        }

        setContent {
            fun isFirstRun(): Boolean {
                val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                val isFirstRun = prefs.getBoolean("isFirstRun", true)
                if (isFirstRun) {
                    with(prefs.edit()) {
                        putBoolean("isFirstRun", false)
                        apply()
                    }
                }
                return isFirstRun
            }

            UWRizzTheme {
                // State to manage whether the user is logged in or not
                val scope = rememberCoroutineScope()
                val dataStore = applicationContext.dataStore
                if (isFirstRun()) {
                    val dbHelper = UserDatabaseHelper(this@MainActivity)
                    dbHelper.addUser("admin", "password") // Replace with your desired credentials
                }

                // Use the dataStore directly inside the MainScreen composable
                MainScreen()
            }
        }
    }
}



@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStore = context.dataStore

    // Use collectAsState to observe the isLoggedIn state
    val isLoggedIn = dataStore.data
        .map { preferences ->
            // Here, we are defaulting to 'false' which means the user is not logged in
            preferences[booleanPreferencesKey("IS_LOGGED_IN")] ?: false
        }
        .collectAsState(initial = false).value
Log.e("checking here :", ""+isLoggedIn)
    if (isLoggedIn) {
        var currentScreen by remember { mutableStateOf(Screen.Home) }
        Scaffold(
            bottomBar = { BottomNavigationBar(currentScreen, onNavigationItemSelected = { screen -> currentScreen = screen }) }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    Screen.Home -> MainContent()
                    Screen.Chat -> ChatScreen()
                    Screen.Likes -> LikesScreen(){ }
                    Screen.Profile -> ProfileSettingsScreen(
                        profileImage = ImageVector.vectorResource(R.drawable.ic_head), // Replace with your actual default image resource
                        onImageClick = {
                            // Define what happens when the "add image" button is clicked
                            // For example, opening a gallery or a photo picker
                        },
                        onNavigateToPreferences = {
                            // Define what happens when "Edit Preferences" button is clicked
                            // E.g., updating the state to navigate to the preferences screen
                            currentScreen = Screen.Preferences
                        },
                        onImageSelected = { uri ->
                            // Here you can handle the selected image URI.
                            // For example, updating the UI state or uploading the image to a server.
                        }
                    )
                    Screen.Preferences -> PreferencesPage(paddingValues = PaddingValues())
                }
            }
        }

    } else {
        // The user is not logged in, show the login screen
        LoginScreen(context = context, onLoginSuccess = {
            // Here we perform the login logic and upon success, we update the dataStore
            scope.launch {
                dataStore.edit { settings ->
                    settings[booleanPreferencesKey("IS_LOGGED_IN")] = true
                }
            }
        })
    }
}


@Composable
fun BottomNavigationBar(currentScreen: Screen, onNavigationItemSelected: (Screen) -> Unit) {
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == Screen.Home,
            onClick = { onNavigationItemSelected(Screen.Home) }
        )
        BottomNavigationItem(
            icon = { Icon(Screen.Chat, contentDescription = "Chat") },
            label = { Text("Chat") },
            selected = currentScreen == Screen.Chat,
            onClick = { onNavigationItemSelected(Screen.Chat) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Likes") },
            label = { Text("Likes") },
            selected = currentScreen == Screen.Likes,
            onClick = { onNavigationItemSelected(Screen.Likes) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentScreen == Screen.Profile,
            onClick = { onNavigationItemSelected(Screen.Profile) }
        )

    }
}

fun Icon(chat: Screen, contentDescription: String) {


}

enum class Screen {
    Home, Chat, Likes, Profile, Preferences
}


