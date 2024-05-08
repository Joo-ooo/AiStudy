package com.example.aistudy.navigation.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.aistudy.ui.screens.chatbot.ChatbotScreen
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Constants.CHATBOT_SCREEN

fun NavGraphBuilder.chatbotComposable(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    navigateToNoteScreen: (Int) -> Unit
) {
    composable(route = CHATBOT_SCREEN) {
        // Now passing navController directly to ChatbotScreen
        ChatbotScreen(
            navigateToNoteScreen = navigateToNoteScreen,
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
}

