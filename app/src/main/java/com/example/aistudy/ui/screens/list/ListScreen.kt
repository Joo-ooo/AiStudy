package com.example.aistudy.ui.screens.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.aistudy.R
import com.example.aistudy.components.note.CustomLoading
import com.example.aistudy.data.models.Note
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Action
import com.example.aistudy.utils.SearchAppBarState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun ListScreen(
    navigateToNoteScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel,
    navigateToARScreen: () -> Unit,
) {

    val action by sharedViewModel.action
    val allNotes = sharedViewModel.allNotes.collectAsLazyPagingItems()
    val searchedNotes = sharedViewModel.searchedNotes.collectAsLazyPagingItems()
    val searchAppBarState: SearchAppBarState by sharedViewModel.searchAppBarState
    val filteredNotes = sharedViewModel.filteredNotes.collectAsLazyPagingItems()
    val categoryFilter by sharedViewModel.categoryFilter.collectAsState()


    LaunchedEffect(key1 = action) {
        sharedViewModel.handleDatabaseAction(action = action)
    }

    val scaffoldState = rememberScaffoldState()

    DisplaySnackBar(
        scaffoldState = scaffoldState,
        onComplete = { sharedViewModel.action.value = it },
        onUndoClicked = {
            sharedViewModel.action.value = it
        },
        noteTitle = sharedViewModel.title.value,
        action = action
    )

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    actionColor = MaterialTheme.colors.primary,
                    snackbarData = data,
                    contentColor = MaterialTheme.colors.primary,
                    backgroundColor = MaterialTheme.colors.secondary
                )
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        topBar = {
            ListAppBar(
                sharedViewModel = sharedViewModel,
                searchAppBarState = searchAppBarState,
                navigateToARScreen = navigateToARScreen
            )
        },
        content = {
            when {
                // When the search is triggered, display the searched notes.
                searchAppBarState == SearchAppBarState.TRIGGERED -> {
                    HandleListContent(
                        notes = searchedNotes,
                        onSwipeToDelete = { action, note ->
                            onSwipeToDelete(
                                sharedViewModel = sharedViewModel,
                                action = action,
                                note = note,
                                scaffoldState = scaffoldState
                            )
                        },
                        navigateToNoteScreen = navigateToNoteScreen,
                        sharedViewModel = sharedViewModel
                    )
                }
                // Handle displaying notes based on the selected category filter.
                categoryFilter != null -> {
                    HandleListContent(
                        notes = filteredNotes, // Use the filteredNotes that get updated when a category is selected
                        onSwipeToDelete = { action, note ->
                            onSwipeToDelete(
                                sharedViewModel = sharedViewModel,
                                action = action,
                                note = note,
                                scaffoldState = scaffoldState
                            )
                        },
                        navigateToNoteScreen = navigateToNoteScreen,
                        sharedViewModel = sharedViewModel
                    )
                }
                // Default case where no specific filter is applied. Show all notes.
                else -> {
                    HandleListContent(
                        notes = allNotes,
                        onSwipeToDelete = { action, note ->
                            onSwipeToDelete(
                                sharedViewModel = sharedViewModel,
                                action = action,
                                note = note,
                                scaffoldState = scaffoldState
                            )
                        },
                        navigateToNoteScreen = navigateToNoteScreen,
                        sharedViewModel = sharedViewModel
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingButton(onFloatingActionButtonPressed = navigateToNoteScreen)
        })
}

private fun onSwipeToDelete(
    scaffoldState: ScaffoldState,
    sharedViewModel: SharedViewModel,
    action: Action,
    note: Note
) {
    sharedViewModel.action.value = action
    sharedViewModel.updateNoteFields(note)
    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
}

@Composable
fun HandleListContent(
    notes: LazyPagingItems<Note>,
    onSwipeToDelete: (Action, Note) -> Unit,
    navigateToNoteScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel
) {
    Log.d("Notes_LOG", notes.loadState.toString())
    if (notes.loadState.refresh == LoadState.Loading) {
        CustomLoading()
    } else {
        if (notes.itemCount == 0) {
            EmptyContent()
        } else {
            ListContent(
                notes = notes,
                onSwipeToDelete = onSwipeToDelete,
                navigateToNoteScreen = navigateToNoteScreen,
                sharedViewModel = sharedViewModel
            )
        }
    }
}


@Composable
fun FloatingButton(onFloatingActionButtonPressed: (taskId: Int) -> Unit) {
    FloatingActionButton(
        modifier = Modifier.padding(end = 8.dp, bottom = 32.dp),
        elevation = FloatingActionButtonDefaults.elevation(20.dp),
        backgroundColor = BlackOlive,
        onClick = { onFloatingActionButtonPressed(-1) }) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(id = R.string.add_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun DisplaySnackBar(
    scaffoldState: ScaffoldState,
    onComplete: (Action) -> Unit,
    onUndoClicked: (Action) -> Unit,
    noteTitle: String,
    action: Action
) {

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = action) {
        if (action != Action.NO_ACTION) {
            scope.launch {
                val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = getSnackBarMessage(action = action, noteTitle = noteTitle),
                    actionLabel = getActionLabel(action)
                )

                undoDeleteTask(
                    action = action,
                    snackBarResult = snackBarResult,
                    onUndoClicked = onUndoClicked
                )
            }
        }
        onComplete(Action.NO_ACTION)
    }
}

private fun getSnackBarMessage(action: Action, noteTitle: String): String {
    return when (action) {
        Action.DELETE_ALL -> {
            "All notes deleted!"
        }
        else -> {
            "${action.name}: $noteTitle"
        }
    }
}

private fun getActionLabel(action: Action): String {
    return if (action.name == "DELETE") {
        "UNDO"
    } else {
        "OK"
    }
}

private fun undoDeleteTask(
    action: Action,
    snackBarResult: SnackbarResult,
    onUndoClicked: (Action) -> Unit
) {
    if (snackBarResult == SnackbarResult.ActionPerformed && action == Action.DELETE) {
        onUndoClicked(Action.UNDO)
    }
}