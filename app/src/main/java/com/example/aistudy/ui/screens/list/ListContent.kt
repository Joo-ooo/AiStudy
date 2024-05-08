package com.example.aistudy.ui.screens.list

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.example.aistudy.R
import com.example.aistudy.components.note.CustomLoading
import com.example.aistudy.components.CustomText
import com.example.aistudy.data.models.Note
import com.example.aistudy.ui.theme.BlackShade
import com.example.aistudy.ui.theme.BorderGray
import com.example.aistudy.ui.theme.OffWhite
import com.example.aistudy.ui.theme.RealBlack
import com.example.aistudy.ui.theme.Red
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Action
import com.example.aistudy.utils.noteItemColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

    /**
     * Implements a composable function `ListContent` for displaying a list of notes with a swipe-to-dismiss functionality.
     * Each note item can be swiped to trigger a deletion action, animated with rotation effects on the delete icon and
     * expand/collapse animations on the note items themselves. The `ListContent` function integrates with a shared ViewModel
     * to manage the state and actions like deletion and navigation to the note detail screen. It handles different loading
     * states for the notes being displayed, including showing a loading indicator when fetching additional notes.
     *
     * Key Features:
     * - Swipe-to-dismiss with animated feedback: Utilizes the `SwipeToDismiss` composable for each note item, providing
     *   users with a clear, interactive method to delete notes. Animation effects include rotating the delete icon and
     *   animating the visibility of note items as they are added or removed.
     * - Dynamic loading state handling: Displays a custom loading indicator when more notes are being fetched and handles
     *   the empty, loading, and error states of note fetching.
     * - Integration with shared ViewModel: Actions like deletion, fetching notes, and navigating to the note detail screen
     *   are managed through a shared ViewModel, ensuring a clean separation of concerns between the UI and data layers.
     * - Flexible navigation and action handling: Includes callbacks for navigating to different screens and performing
     *   actions like deleting all notes or filtering by category.
     * - Accessibility support: Provides content descriptions for icons and interactive elements, making the app more
     *   accessible to users utilizing screen readers.
     *
     * Components:
     * - `ListContent`: Main composable function that displays the list of notes.
     * - `SwipeItemBackground`: Background component shown during swipe actions.
     * - `NoteItem`: Individual note item with detailed view and actions.
     */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListContent(
    notes: LazyPagingItems<Note>,
    onSwipeToDelete: (Action, Note) -> Unit,
    navigateToNoteScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel
) {


    Log.d("LazyPagingItems_LoadState_APPEND", notes.loadState.append.toString())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        itemsIndexed(notes, key = { _, note -> note.id }) { index, note ->
            note?.let {
                val dismissState = rememberDismissState()
                val dismissDirection = dismissState.dismissDirection
                val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)

                if (isDismissed && dismissDirection == DismissDirection.EndToStart) {
                    val scope = rememberCoroutineScope()
                    scope.launch {
                        delay(500)
                        onSwipeToDelete(Action.DELETE, note)
                    }
                }

                val degrees by animateFloatAsState(
                    targetValue = if (dismissState.targetValue == DismissValue.Default) 0f
                    else -45f
                )

                var itemAppeared by remember {
                    mutableStateOf(false)
                }

                LaunchedEffect(key1 = true) {
                    itemAppeared = true
                }

                AnimatedVisibility(
                    visible = itemAppeared && !isDismissed,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 500)
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 500)
                    )
                ) {
                    SwipeToDismiss(modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp)),
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        dismissThresholds = { FractionalThreshold(0.2f) },
                        background = { SwipeItemBackground(degrees = degrees) },
                        dismissContent = {
                            NoteItem(
                                note = note,
                                index = index,
                                navigateToNoteScreen = navigateToNoteScreen,
                                sharedViewModel = sharedViewModel
                            )
                        })
                }
            }
        }

        // Handles different loading states when appending new items to the list
        when (notes.loadState.append) {
            is LoadState.Loading -> {
                item {
                    CustomLoading()
                }
            }
            is LoadState.NotLoading -> Unit
            is LoadState.Error -> Unit
        }
    }
}

@Composable
fun SwipeItemBackground(degrees: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Red), contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            modifier = Modifier
                .rotate(degrees = degrees)
                .padding(end = 16.dp),
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.delete_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteItem(
    note: Note, index: Int, navigateToNoteScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel
) {
    val roundedCornerShape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 3.dp, color = BorderGray, shape = roundedCornerShape)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(roundedCornerShape),
            color = noteItemColor(index = index),
            elevation = 10.dp,
            shape = roundedCornerShape,
            onClick = { navigateToNoteScreen(note.id) }
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    CustomText(
                        text = note.title,
                        color = BlackShade,
                        fontWeight = FontWeight.W700,
                        fontSize = 22.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // CATEGORY BOX RIGHT HERE
                    val coroutineScope = rememberCoroutineScope()
                    var categoryName by remember { mutableStateOf("") }

                    // Fetch the category name when the composable first loads or when the note's categoryId changes
                    LaunchedEffect(key1 = note.categoryId) {
                        coroutineScope.launch {
                            categoryName = sharedViewModel.fetchCategoryName(note.categoryId)
                        }
                    }

                    if (categoryName.isNotEmpty()) {
                        // This box contains the category name and its border
                        Box(
                            modifier = Modifier
                                .background(
                                    color = RealBlack,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = RealBlack,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            CustomText(
                                modifier = Modifier.padding(2.dp),
                                text = categoryName,
                                color = OffWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    }


                CustomText(
                    modifier = Modifier.padding(top = 8.dp),
                    text = note.description,
                    color = BlackShade,
                    fontWeight = FontWeight.W400,
                    fontSize = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // DATE TEXT RIGHT HERE
                CustomText(
                    modifier = Modifier.padding(top = 12.dp),
                    text = SimpleDateFormat(
                        "E, dd MMM yyyy",
                        Locale.ENGLISH
                    ).format(note.updatedAt),
                    color = Color.DarkGray,
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
@Preview
fun NoteItemPreview() {
    NoteItem(note = Note(
        id = 0,
        title = "Book Review : The Design of Everyday Things by Don Norman",
        description = "Book Review : The Design of Everyday Things by Don Norman",
        reminderDateTime = null,
        workerRequestId = null,
        createdAt = Date(),
        updatedAt = Date(),
        categoryId = 1
    ),
        index = 0,
        navigateToNoteScreen = {},
        sharedViewModel = viewModel())
}

@Composable
@Preview
fun CustomLoadingPreview() {
    CustomLoading()
}

