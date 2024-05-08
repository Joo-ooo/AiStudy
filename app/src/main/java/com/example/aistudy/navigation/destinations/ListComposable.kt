package com.example.aistudy.navigation.destinations

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aistudy.ui.screens.list.ListScreen
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Action
import com.example.aistudy.utils.Constants.LIST_ARGUMENT_KEY
import com.example.aistudy.utils.Constants.LIST_SCREEN
import com.example.aistudy.utils.toAction

fun NavGraphBuilder.listComposable(
    navigateToNoteScreen: (Int) -> Unit,
    sharedViewModel: SharedViewModel,
    navigateToARScreen: () -> Unit
) {
    composable(route = LIST_SCREEN, arguments = listOf(navArgument(LIST_ARGUMENT_KEY) {
        type = NavType.StringType
    })) { navBackStackEntry ->
        val triggeredAction = navBackStackEntry.arguments?.getString(LIST_ARGUMENT_KEY).toAction()

        var lastTriggeredAction by rememberSaveable {
            mutableStateOf(Action.NO_ACTION)
        }

        LaunchedEffect(key1 = triggeredAction) {
            if (triggeredAction != lastTriggeredAction) {
                lastTriggeredAction = triggeredAction
                sharedViewModel.action.value = triggeredAction
            }
        }

        ListScreen(
            navigateToNoteScreen = navigateToNoteScreen,
            sharedViewModel = sharedViewModel,
            navigateToARScreen = navigateToARScreen
        )
    }
}