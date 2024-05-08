package com.example.aistudy.ui.screens.note

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.aistudy.R
import com.example.aistudy.data.models.Note
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.ui.theme.Blue
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.Action
import java.util.*


    /**
     * This screen incorporates comprehensive functionality including title and description input,
     * category selection, setting reminders, and navigation to additional features like chatbot, image to text, and
     * speech to text conversion. It uses a shared ViewModel to manage note-related data and validates input fields
     * before performing save or update actions.
     *
     * Key Features:
     * - Dynamic content based on the selected note for editing or empty fields for adding a new note.
     * - Validation of note fields with feedback provided through toasts for user corrections.
     * - Integration with other app features like chatbot, image to text, and speech to text through navigation callbacks.
     * - Use of Material Design components like Scaffold and FloatingActionButton for consistent UI design.
     *
     * Components:
     * - NoteAppBar: A top app bar with navigation and action buttons, handling note saving, updating, and navigation.
     * - NoteContent: The main content area where users can input note details like title, description, and set reminders.
     * - FloatingButton: A floating action button that triggers note validation and saving or updating actions.
     * - displayToast: A utility function for displaying toasts with customized styling for user feedback.
     */

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteScreen(
    navigateToListScreen: (Action) -> Unit,
    navigateToChatbotScreen: () -> Unit,
    navigateToImage2TextScreen: () -> Unit,
    selectedNote: Note?,
    sharedViewModel: SharedViewModel,
    navigateToSpeech2TextScreen: (Int) -> Unit
) {
    val title: String by sharedViewModel.title
    val description: String by sharedViewModel.description
    val categoryId: Int by sharedViewModel.categoryId
    val reminderDateTime: Date? by sharedViewModel.reminderDateTime

    val context = LocalContext.current
    val validationErrorMessage = stringResource(id = R.string.note_validation_error)

    BackHandler {
        navigateToListScreen(Action.NO_ACTION)
    }

    Scaffold(backgroundColor = MaterialTheme.colors.primary, topBar = {
        NoteAppBar(
            navigateToListScreen = { action ->
                if (action == Action.NO_ACTION || sharedViewModel.validateNoteFields()) {
                    navigateToListScreen(action)
                } else {
                    displayToast(
                        context = context, message = validationErrorMessage
                    )
                }
            }, selectedNote = selectedNote,
            navigateToChatbotScreen = navigateToChatbotScreen,
            navigateToImage2TextScreen = navigateToImage2TextScreen,
            navigateToSpeech2TextScreen = navigateToSpeech2TextScreen,
            sharedViewModel = sharedViewModel
        )
    }, content = {
        NoteContent(
            title = title,
            onTitleChange = { title ->
                sharedViewModel.title.value = title
            },
            description = description,
            onDescriptionChange = { description ->
                sharedViewModel.description.value = description
            },
            categoryId =  categoryId,
            onCategoryChange = { categoryId ->
                sharedViewModel.categoryId.value = categoryId
            },
            reminderDateTime = reminderDateTime,
            onReminderDateTimeChange = { date ->
                sharedViewModel.reminderDateTime.value = date
            },
            sharedViewModel = sharedViewModel,
            selectedNote = selectedNote,
            navigateToSpeech2TextScreen = navigateToSpeech2TextScreen
        )
    },
        floatingActionButton = {
            val action = if (selectedNote == null) Action.ADD else Action.UPDATE
            FloatingButton(
                action = action,
                navigateToListScreen = navigateToListScreen,
                validateNoteFields = sharedViewModel::validateNoteFields,
                displayToast = ::displayToast,
                context = context,
                validationErrorMessage = validationErrorMessage
            )
        }
    )
}

fun displayToast(context: Context, message: String) {
    val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

    val view: View? = toast.getView();

    view?.getBackground()?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

    val text: TextView? = view?.findViewById(android.R.id.message);

    text?.setTextColor(Color.BLACK);
    text?.setBackgroundColor(Color.TRANSPARENT);

    toast.show();
}

@Composable
fun FloatingButton(
    action: Action,
    navigateToListScreen: (Action) -> Unit,
    validateNoteFields: () -> Boolean,
    displayToast: (Context, String) -> Unit,
    context: Context,
    validationErrorMessage: String
) {
    FloatingActionButton(
        modifier = Modifier.padding(end = 8.dp, bottom = 32.dp),
        elevation = FloatingActionButtonDefaults.elevation(20.dp),
        backgroundColor = BlackOlive,
        onClick = {
            // Perform the validation check when the action is not NO_ACTION
            if (action == Action.NO_ACTION || validateNoteFields()) {
                navigateToListScreen(action)
            } else {
                displayToast(context, validationErrorMessage)
            }
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = stringResource(id = R.string.add_note_action),
            tint = Blue
        )
    }
}
