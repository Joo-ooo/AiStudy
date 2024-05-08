package com.example.aistudy.navigation.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.aistudy.ui.screens.augmentedreality.ARScreen
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Action
import com.example.aistudy.utils.Constants.AR_SCREEN

fun NavGraphBuilder.ARComposable(
    navigateToListScreen: (Action) -> Unit,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    composable(route = AR_SCREEN) {
        ARScreen(
            navigateToListScreen = navigateToListScreen,
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
}
