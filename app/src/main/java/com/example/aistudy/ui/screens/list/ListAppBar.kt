package com.example.aistudy.ui.screens.list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aistudy.R
import com.example.aistudy.components.CustomText
import com.example.aistudy.components.note.DisplayAlertDialog
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.ui.theme.ChineseSilver
import com.example.aistudy.ui.theme.fontFamily
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.APP_BAR_HEIGHT
import com.example.aistudy.utils.Action
import com.example.aistudy.utils.SearchAppBarState
import com.example.aistudy.data.models.Category
import com.example.aistudy.ui.theme.logoRed

/**
 * The ListAppBar function dynamically updates the app bar's layout based on the current state (CLOSED or OPENED for search).
 * It leverages the shared ViewModel to manage these states and perform actions like deleting all notes or filtering by category.
 *
 * DefaultListAppBar and ListAppBarActions showcase how to compose a functional and interactive app bar with customized actions that integrate
 * seamlessly with the rest of the application, enhancing the user experience through intuitive navigation and management of notes.
 *
 * The SearchAppBar function enhances the search functionality by providing a dedicated UI for inputting search queries, complete with a clear button
 * and reactive search icon behavior.
 *
 * Action composable functions like SearchAction and ARAction demonstrate the use of Box and Icon composables to create interactive areas within
 * the app bar, responding to user taps to trigger defined actions.
 *
 * The CategoryFilterAction includes a dropdown menu for filtering notes by category, illustrating how to use DropdownMenu and
 * DropdownMenuItem composables for creating contextual menus within the app.
 *
 * DeleteAllAction introduces dialog interaction within the composable UI, showing how to prompt users for confirmation before
 * executing an action like deleting all notes, enhancing data management within the app.
 */

@Composable
fun ListAppBar(
    sharedViewModel: SharedViewModel,
    searchAppBarState: SearchAppBarState,
    navigateToARScreen:() -> Unit
) {
    val categories by sharedViewModel.categories.collectAsState()

    when (searchAppBarState) {
        SearchAppBarState.CLOSED -> {
            DefaultListAppBar(
                onSearchIconPressed = {
                    sharedViewModel.searchAppBarState.value = SearchAppBarState.OPENED
                },
                sharedViewModel = sharedViewModel,
                navigateToARScreen = navigateToARScreen,
                categories = categories,
                deleteAllNotes = {
                    sharedViewModel.action.value = Action.DELETE_ALL
                }
            )
        }
        else -> {
            SearchAppBar(
                onSearchPressed = { text ->
                    sharedViewModel.searchTextState.value = text
                    sharedViewModel.searchNotes()
                },
                onClosePressed = {
                    sharedViewModel.searchAppBarState.value = SearchAppBarState.CLOSED
                    sharedViewModel.searchTextState.value = ""
                })
        }
    }


}

@Composable
fun DefaultListAppBar(
    onSearchIconPressed: () -> Unit,
    navigateToARScreen: () -> Unit,
    categories: List<Category>,
    deleteAllNotes: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    TopAppBar(
        elevation = 0.dp, modifier = Modifier.padding(start = 7.dp), title = {
        CustomText2(
            text = "AI",
            color = logoRed,
            fontSize = 32.sp,
            fontWeight = FontWeight.W600
            )
        CustomText(
            text = "Study",
            color = MaterialTheme.colors.secondary,
            fontSize = 32.sp,
            fontWeight = FontWeight.W600,
        )
    }, backgroundColor = MaterialTheme.colors.primary, actions = {
        ListAppBarActions(
            onSearchIconPressed = onSearchIconPressed,
            navigateToARScreen = navigateToARScreen,
            categories = categories,
            deleteAllNotes = deleteAllNotes,
            sharedViewModel = sharedViewModel
        )
    })
}

@Composable
fun ListAppBarActions(
    onSearchIconPressed: () -> Unit,
    navigateToARScreen: () -> Unit,
    categories: List<Category>,
    deleteAllNotes: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    SearchAction(onSearchIconPressed = onSearchIconPressed)
    Divider(modifier = Modifier.width(16.dp), color = MaterialTheme.colors.primary)
    ARAction(navigateToARScreen = navigateToARScreen)
    Divider(modifier = Modifier.width(16.dp), color = MaterialTheme.colors.primary)
    CategoryFilterAction(categories = categories, sharedViewModel = sharedViewModel)
    Divider(modifier = Modifier.width(16.dp), color = MaterialTheme.colors.primary)
    DeleteAllAction(deleteAllNotes = deleteAllNotes)
    Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
}

@Composable
fun SearchAction(onSearchIconPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { onSearchIconPressed() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(id = R.string.search_notes_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun CategoryFilterAction(
    categories: List<Category>,
    sharedViewModel: SharedViewModel // Inject the SharedViewModel into the composable
) {
    var expanded by remember { mutableStateOf(false) }

    val filteredNotes by sharedViewModel.filteredNotes.collectAsState()

    LaunchedEffect(key1 = filteredNotes) {
        Log.d("Composable", "FilteredNotesList recomposed with filtered notes.")
    }

    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { expanded = true },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter_list),
            contentDescription = stringResource(id = R.string.sort_notes_action),
            tint = MaterialTheme.colors.secondary
        )

        DropdownMenu(
            modifier = Modifier.background(BlackOlive),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            // Option for "None" to clear the filter
            DropdownMenuItem(onClick = {
                // Clear the category filter
                sharedViewModel.setCategoryFilter(null)
                expanded = false
            }) {
                CustomText(
                    text = "Clear Filter",
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp
                )
            }

            Divider()

            // Category options
            categories.forEach { category ->
                DropdownMenuItem(onClick = {
                    // Apply the category filter
                    sharedViewModel.setCategoryFilter(category.id)
                    expanded = false
                }) {
                    CustomText(
                        text = category.name,
                        color = MaterialTheme.colors.secondary,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}


@Composable
fun DeleteAllAction(deleteAllNotes: () -> Unit) {

    var expanded by remember {
        mutableStateOf(false)
    }

    var openDialog by remember {
        mutableStateOf(false)
    }

    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_all_notes_alert_title),
        message = stringResource(id = R.string.delete_all_notes_alert_message),
        openDialog = openDialog,
        button1Text = "No",
        button2Text = "Yes",
        onButton1Pressed = { openDialog = false },
        onButton2Pressed = {
            openDialog = false
            deleteAllNotes()
        })

    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { expanded = true },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(id = R.string.delete_all_notes_action),
            tint = MaterialTheme.colors.secondary
        )

        DropdownMenu(
            modifier = Modifier.background(BlackOlive),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = {
                openDialog = true
                expanded = false
            }) {
                CustomText(
                    text = stringResource(id = R.string.delete_all_notes_action),
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SearchAppBar(
    onClosePressed: () -> Unit,
    onSearchPressed: (String) -> Unit
) {
    var searchText by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(APP_BAR_HEIGHT),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(color = BlackOlive),
            value = searchText,
            onValueChange = { text ->
                searchText = text
            },
            placeholder = {
                CustomText(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.search_placeholder),
                    color = ChineseSilver,
                    fontWeight = FontWeight.W300,
                    fontSize = 18.sp
                )
            },
            textStyle = TextStyle(
                color = MaterialTheme.colors.secondary,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 18.sp
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium)
                        .padding(start = 8.dp),
                    onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search_icon),
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            trailingIcon = {
                IconButton(modifier = Modifier
                    .alpha(ContentAlpha.medium)
                    .padding(end = 8.dp),
                    onClick = {
                        if (searchText.isEmpty()) {
                            onClosePressed()
                        } else {
                            searchText = ""
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close_icon),
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchPressed(searchText)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colors.secondary,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent
            )
        )
    }
}

@Composable
fun ARAction(navigateToARScreen: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { navigateToARScreen() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_view_in_ar),
            contentDescription = stringResource(id = R.string.ar_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}


@Composable
@Preview
private fun DefaultListAppBarPreview() {
    SearchAppBar(onSearchPressed = {}, onClosePressed = {})
    //DefaultListAppBar(onSearchClicked = {}, onSortClicked = {}, onDeleteClicked = {})
}

@Composable
fun CustomText2(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = FontStyle.Italic // Added for italics
    )
}
