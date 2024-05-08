package com.example.aistudy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.aistudy.navigation.destinations.ARComposable
import com.example.aistudy.navigation.destinations.Image2TextComposable
import com.example.aistudy.navigation.destinations.Speech2TextComposable
import com.example.aistudy.navigation.destinations.chatbotComposable
import com.example.aistudy.navigation.destinations.listComposable
import com.example.aistudy.navigation.destinations.noteComposable
import com.example.aistudy.navigation.destinations.splashComposable
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Constants.LIST_SCREEN

    /**
     * Defines the navigation graph for the application, mapping composable screens to routes and
     * configuring navigation actions between them. This function utilizes a NavHostController to
     * manage navigation operations and a SharedViewModel to handle shared data across composable
     * screens. The navigation setup includes routes for a splash screen, a list screen, note details,
     * a chatbot interface, image to text conversion, speech to text conversion, and an augmented reality (AR) screen.
     *
     * @param navController The NavHostController that manages navigation within the NavHost.
     * @param sharedViewModel The SharedViewModel that is used across multiple composables to manage shared data.
     */
@Composable
fun SetupNavigation(navController: NavHostController, sharedViewModel: SharedViewModel) {
    val screenRoutes = remember(navController) {
        ScreenRoutes(navController = navController)
    }

    NavHost(navController = navController, startDestination = LIST_SCREEN) {
        splashComposable(
            navigateToListScreen = screenRoutes.fromSplashToList
        )
        listComposable(
            navigateToNoteScreen = screenRoutes.fromFunctiontoNote,
            sharedViewModel = sharedViewModel,
            navigateToARScreen = screenRoutes.fromListToAR
        )
        noteComposable(
            navigateToListScreen = screenRoutes.fromNoteToList,
            navigateToChatbotScreen = screenRoutes.fromNoteToChatbot,
            navigateToImage2TextScreen = screenRoutes.fromNoteToImage2Text,
            navigateToSpeech2TextScreen = screenRoutes.fromNoteToSpeech2Text,
            sharedViewModel = sharedViewModel
        )
        chatbotComposable(
            navigateToNoteScreen = screenRoutes.fromFunctiontoNote,
            sharedViewModel = sharedViewModel,
            navController = navController
        )
        Image2TextComposable(
            navigateToNoteScreen = screenRoutes.fromFunctiontoNote,
            sharedViewModel = sharedViewModel,
            navController = navController
        )
        Speech2TextComposable(
            navigateToNoteScreen = screenRoutes.fromFunctiontoNote,
            sharedViewModel = sharedViewModel,
            navController = navController
        )
        ARComposable(
            navigateToListScreen = screenRoutes.fromARtoList,
            navController = navController,
            sharedViewModel = sharedViewModel

        )

    }
}