package com.example.aistudy.navigation.destinations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aistudy.ui.screens.Image2Text.Image2TextScreen
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Constants.IMAGE2TEXT_SCREEN

fun NavGraphBuilder.Image2TextComposable(
    sharedViewModel: SharedViewModel,
    navigateToNoteScreen: (Int) -> Unit,
    navController: NavHostController
) {
    composable(route = IMAGE2TEXT_SCREEN) { backStackEntry ->
        Image2TextScreen(
            navController = navController,
            sharedViewModel = sharedViewModel,
            navigateToNoteScreen = navigateToNoteScreen
        )
    }
}
