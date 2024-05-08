package com.example.aistudy.navigation

import androidx.navigation.NavController
import com.example.aistudy.utils.Action
import com.example.aistudy.utils.Constants.AR_SCREEN
import com.example.aistudy.utils.Constants.CHATBOT_SCREEN
import com.example.aistudy.utils.Constants.IMAGE2TEXT_SCREEN
import com.example.aistudy.utils.Constants.LIST_SCREEN
import com.example.aistudy.utils.Constants.SPEECH2TEXT_SCREEN
import com.example.aistudy.utils.Constants.SPLASH_SCREEN

    /**
     * ScreenRoutes centralizes navigation logic for an application using Jetpack Compose Navigation.
     * It provides a set of lambda properties, each representing a specific navigation action
     * within the app, allowing for clear and concise navigation calls. These actions cover transitions
     * between a variety of screens, including note lists, AR views, chatbots, image-to-text and
     * speech-to-text conversion screens, and more. This approach encapsulates navigation commands
     * and routes, promoting reusability and reducing boilerplate in UI components.
     * Each navigation action is defined as a lambda function that performs navigation using a provided
     * NavController instance. This setup simplifies navigation logic by abstracting the details
     * of constructing navigation routes and managing the navigation stack.
     */

class ScreenRoutes(navController: NavController) {

    val fromNoteToList: (Action) -> Unit = { action ->
        navController.navigate(route = "list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }

    val fromSplashToList: () -> Unit = {
        navController.navigate(route = "list/${Action.NO_ACTION.name}") {
            popUpTo(SPLASH_SCREEN) { inclusive = true }
        }
    }

    val fromListToAR: () -> Unit = {
        navController.navigate(route = AR_SCREEN)
    }

    val fromARtoList:  (Action) -> Unit = { action ->
        navController.navigate(route = "list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }

    val fromNoteToChatbot: () -> Unit = {
        navController.navigate(route = CHATBOT_SCREEN)
    }

    val fromNoteToImage2Text: () -> Unit = {
        navController.navigate(route = IMAGE2TEXT_SCREEN)
    }

    val fromNoteToSpeech2Text: (Int) -> Unit = { transcriptId ->
        navController.navigate(route = "speech2text/$transcriptId")
    }

    val fromFunctiontoNote: (Int) -> Unit = { noteId ->
        navController.navigate(route = "note/$noteId")
    }

}
