package com.example.aistudy

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.aistudy.navigation.SetupNavigation
import com.example.aistudy.ui.theme.NotesTheme
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.GlobalVariable
import dagger.hilt.android.AndroidEntryPoint

    /**
     * MainActivity is configured to use Hilt for dependency injection.
     * It sets up the application's navigation and manages the splash screen and notification permissions.
     *
     * Lifecycle:
     * - onCreate(savedInstanceState: Bundle?): The first callback in the activity lifecycle. It initializes the activity,
     *   sets up the splash screen condition based on the SharedViewModel, requests notification permissions on TIRAMISU
     *   and above, creates a notification channel, and sets the content view using Compose.
     *
     * Functions:
     * - requestNotificationPermission(): Checks and requests the POST_NOTIFICATIONS permission from the user. This is
     *   necessary for the app to display notifications on Android TIRAMISU (API 33) and above.
     * - createNotificationChannel(): Creates a notification channel for the app. Notification channels are necessary for
     *   delivering notifications to users starting from Android O (API 26). This method defines the channel's importance
     *   and its visual and behavioral aspects.
     */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var sharedViewModel: SharedViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                sharedViewModel.shouldShowSplashScreen.value
            }
        }

        if (sharedViewModel.shouldShowSplashScreen.value) {
            requestNotificationPermission()
        }

        // Create a notification channel (for Android O and above)
        createNotificationChannel()

        setContent {
            NotesTheme {
                navController = rememberNavController()
                SetupNavigation(navController = navController, sharedViewModel = sharedViewModel)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                Log.d("NOTIFICATION_PERMISSION", isGranted.toString())
                GlobalVariable.hasNotificationPermission = isGranted
            }
        requestPermissionLauncher.launch(
            POST_NOTIFICATIONS
        )
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name) // Example: "Transcript Ready"
        val descriptionText = getString(R.string.channel_description) // Example: "Notifications for when transcripts are ready"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
