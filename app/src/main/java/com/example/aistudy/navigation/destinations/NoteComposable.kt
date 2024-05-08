package com.example.aistudy.navigation.destinations

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.aistudy.ui.screens.note.NoteScreen
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Action
import com.example.aistudy.utils.Constants.NOTE_ARGUMENT_KEY
import com.example.aistudy.utils.Constants.NOTE_SCREEN

fun NavGraphBuilder.noteComposable(
    navigateToListScreen: (Action) -> Unit,
    sharedViewModel: SharedViewModel,
    navigateToChatbotScreen: () -> Unit,
    navigateToImage2TextScreen: () -> Unit,
    navigateToSpeech2TextScreen: (Int) -> Unit
) {
    composable(route = NOTE_SCREEN, arguments = listOf(navArgument(NOTE_ARGUMENT_KEY) {
        type = NavType.IntType
    })) { navBackStackEntry ->

        val noteId = navBackStackEntry.arguments!!.getInt(NOTE_ARGUMENT_KEY)

        val selectedNote by sharedViewModel.selectedNote.collectAsState()

        LaunchedEffect(key1 = noteId) {
            sharedViewModel.getSelectedNote(noteId = noteId)
        }

        LaunchedEffect(key1 = selectedNote) {
            if (selectedNote != null || noteId == -1) {
                sharedViewModel.updateNoteFields(selectedNote = selectedNote)
            }
        }

        NoteScreen(
            navigateToListScreen = navigateToListScreen,
            selectedNote = selectedNote,
            sharedViewModel = sharedViewModel,
            navigateToChatbotScreen = navigateToChatbotScreen,
            navigateToImage2TextScreen = navigateToImage2TextScreen,
            navigateToSpeech2TextScreen = navigateToSpeech2TextScreen)
    }
}