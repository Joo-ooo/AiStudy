package com.example.aistudy.navigation.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aistudy.ui.screens.Speech2TextScreen.Speech2TextScreen
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Constants.SPEECH2TEXT_SCREEN

fun NavGraphBuilder.Speech2TextComposable(
    navigateToNoteScreen: (Int) -> Unit,
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
) {
    composable(
        route = SPEECH2TEXT_SCREEN,
        arguments = listOf(navArgument("transcriptId") { type = NavType.IntType })
    ) { backStackEntry ->
        // Retrieve the transcriptId from the back stack entry
        val transcriptId = backStackEntry.arguments?.getInt("transcriptId") ?: throw IllegalArgumentException("Transcript ID not found")
        Speech2TextScreen(
            transcriptId = transcriptId,
            navigateToNoteScreen = navigateToNoteScreen,
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
}