package com.example.aistudy.ui.screens.note

import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aistudy.components.CustomText
import com.example.aistudy.components.note.DisplayAlertDialog
import com.example.aistudy.data.models.Note
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.utils.Action
import com.example.aistudy.R
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.aistudy.MainActivity
import com.example.aistudy.ui.theme.ChineseSilver
import com.example.aistudy.ui.viewmodels.SharedViewModel
import java.io.OutputStream

@Composable
fun NoteAppBar(
    navigateToListScreen: (Action) -> Unit,
    navigateToChatbotScreen: () -> Unit,
    navigateToImage2TextScreen: () -> Unit,
    navigateToSpeech2TextScreen:(Int) -> Unit,
    selectedNote: Note?,
    sharedViewModel: SharedViewModel
) {
    if (selectedNote == null) {
        NewNoteAppBar(navigateToListScreen = navigateToListScreen)
    } else {
        EditNoteAppBar(note = selectedNote, navigateToListScreen = navigateToListScreen, navigateToChatbotScreen = navigateToChatbotScreen, navigateToImage2TextScreen = navigateToImage2TextScreen, navigateToSpeech2TextScreen = navigateToSpeech2TextScreen, sharedViewModel = sharedViewModel)
    }
}


@Composable
fun NewNoteAppBar(navigateToListScreen: (Action) -> Unit) {
    TopAppBar(
        elevation = 0.dp,
        navigationIcon = {
            Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
            BackButton(backButtonPressed = navigateToListScreen)
        },
        title = {
            CustomText(
                text = stringResource(id = R.string.add_note),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
        }
    )
}

@Composable
fun BackButton(backButtonPressed: (Action) -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { backButtonPressed(Action.NO_ACTION) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(id = R.string.back_arrow),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun AddNoteButton(addNoteButtonPressed: (Action) -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { addNoteButtonPressed(Action.ADD) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_save),
            contentDescription = stringResource(id = R.string.save_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun EditNoteAppBar(note: Note, navigateToListScreen: (Action) -> Unit, navigateToChatbotScreen: () -> Unit, navigateToImage2TextScreen: () -> Unit, navigateToSpeech2TextScreen: (Int) -> Unit, sharedViewModel: SharedViewModel) {
    TopAppBar(
        elevation = 0.dp,
        navigationIcon = {
            Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
            BackButton(backButtonPressed = navigateToListScreen)
        },
        title = {
            CustomText(
                text = stringResource(id = R.string.edit_note),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            EditNoteAppBarActions(note = note, navigateToListScreen = navigateToListScreen, navigateToChatbotScreen = navigateToChatbotScreen, navigateToImage2TextScreen = navigateToImage2TextScreen, navigateToSpeech2TextScreen = navigateToSpeech2TextScreen, sharedViewModel = sharedViewModel, selectedNote = note)
            Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
        }
    )
}

@Composable
fun EditNoteAppBarActions(note: Note, navigateToListScreen: (Action) -> Unit, navigateToChatbotScreen: () -> Unit, navigateToImage2TextScreen: () -> Unit, navigateToSpeech2TextScreen: (Int) -> Unit, sharedViewModel: SharedViewModel, selectedNote: Note?){
    var openDialog by remember {
        mutableStateOf(false)
    }

    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_note_alert_title),
        message = stringResource(id = R.string.delete_note_alert_message),
        openDialog = openDialog,
        button1Text = "No",
        button2Text = "Yes",
        onButton1Pressed = { openDialog = false },
        onButton2Pressed = {
            openDialog = false
            navigateToListScreen(Action.DELETE)
        })

    ChatbotButton(navigateToChatbotScreen = navigateToChatbotScreen)
    Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
    AddPhotoButton(
        onImagePicked = { uri ->
            sharedViewModel.addPhoto(selectedNote,uri.toString())
        }
    )
    Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
    AddToTextButton(navigateToImage2TextScreen = navigateToImage2TextScreen, navigateToSpeech2TextScreen = navigateToSpeech2TextScreen, sharedViewModel = sharedViewModel, selectedNote = selectedNote)
    Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
}

@Composable
fun EditNoteButton(editNoteButtonPressed: (Action) -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { editNoteButtonPressed(Action.UPDATE) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_save),
            contentDescription = stringResource(id = R.string.edit_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun DeleteNoteButton(deleteNoteButtonPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { deleteNoteButtonPressed() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = stringResource(id = R.string.delete_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun AddToTextButton(
    navigateToImage2TextScreen: () -> Unit,
    navigateToSpeech2TextScreen: (Int) -> Unit,
    sharedViewModel: SharedViewModel,
    selectedNote: Note?
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current // Get the current context

    // Assuming transcriptId is obtained correctly after processing
    fun createNotification(transcriptId: Int) {
        val channelId = "transcript_ready_channel"
        val notificationId = 1

        // Create an intent to launch the activity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("transcriptId", transcriptId) // Pass the transcript ID
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
            .setContentTitle("Your transcript is ready!")
            .setContentText("Tap to view your transcript.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Removes notification after tapping

        try {
            with(NotificationManagerCompat.from(context)) {
                // NotificationId is a unique int for each notification
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            Log.e("NotificationService", "Failed to post notification due to security restrictions", e)
        }
    }

    val pickFileResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Handle the picked file
        uri?.let { selectedUri ->
            sharedViewModel.setTranscriptFilePath(context, selectedUri) { transcriptId ->
                // Notification to be shown after processing the transcript
                createNotification(transcriptId)
                sharedViewModel.addTranscript(selectedNote, transcriptId)
            }
        }
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
            painter = painterResource(id = R.drawable.ic_add_to_text),
            contentDescription = stringResource(id = R.string.add_text_action),
            tint = MaterialTheme.colors.secondary
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp)
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    navigateToImage2TextScreen()
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdownimg), // Assuming you have ic_dropdownimg in your drawable resources
                        contentDescription = "Image-to-Text",
                        tint = ChineseSilver,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomText("Image-to-Text", color = ChineseSilver)
                }
            }
            Divider(color = ChineseSilver.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.width(170.dp).align(Alignment.CenterHorizontally)) // You can adjust the alpha and thickness as needed
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    pickFileResultLauncher.launch("audio/wav")
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdownaudio), // Assuming you have ic_dropdownaudio in your drawable resources
                        contentDescription = "Audio-to-Text",
                        tint = ChineseSilver,
                    )
                    Spacer(modifier = Modifier.width(11.dp))
                    CustomText("Audio-to-Text", color = ChineseSilver)
                }
            }
        }
    }
}

@Composable
fun AddPhotoButton(
    onImagePicked: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Remember a launcher for permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, proceed with opening camera or gallery
                showDialog = true
            } else {
                Toast.makeText(context, "Camera permission is required to take photos.", Toast.LENGTH_LONG).show()
            }
        }
    )

// Remember a launcher for taking a picture and saving it
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            bitmap?.let {
                // Assuming you have a function to save the bitmap to storage and return a Uri
                val savedUri = saveBitmapAndGetUri(context, it)
                // Once the bitmap is saved and you have a Uri, you can use onImagePicked to handle the Uri
                onImagePicked(savedUri)
            }
        }
    )

    // Remember a launcher for picking an image
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onImagePicked
    )

    // This checks for permission and shows the dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showDialog = false
                            takePictureLauncher.launch(null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Filled.PhotoCamera, contentDescription = "Camera", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        CustomText(text = "Take Photo", color = Color.White)
                    }
                    TextButton(
                        onClick = {
                            showDialog = false
                            pickImageLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Filled.PhotoLibrary, contentDescription = "Camera", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        CustomText(text = "Pick from Gallery", color = Color.White)
                    }
                }
            },
            buttons = { },
            backgroundColor = MaterialTheme.colors.primarySurface,
            contentColor = Color.White
        )
    }

    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable {
                // Check for camera permission before showing dialog
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.CAMERA
                    ) -> {
                        showDialog = true
                    }
                    else -> {
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add_photo),
            contentDescription = stringResource(id = R.string.add_photo_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
    val filename = "photo_${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null
    var uri: Uri? = null
    context.contentResolver?.also { resolver ->
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = uri?.let { resolver.openOutputStream(it) }
    }

    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }

    return uri
}




@Composable
fun ChatbotButton(navigateToChatbotScreen: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
                .clickable { navigateToChatbotScreen() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chatbot),
            contentDescription = stringResource(id = R.string.chatbot_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

//@Composable
//@Preview
//fun NoteAppBarPreview(
//) {
//    NoteAppBar(
//        navigateToListScreen = { /* handle navigation */ },
//        navigateToChatbotScreen = { /* handle navigation */ },
//        selectedNote = null,
//        navigateToImage2TextScreen = { /* handle navigation */ },
//        navigateToSpeech2TextScreen = { /* handle navigation */ },
//        sharedViewModel = SharedViewModel
//    )
//}
//
//@Composable
//@Preview
//fun EditNoteAppBarPreview() {
//    EditNoteAppBar(
//        note = Note(
//            id = 0,
//            title = "UI concepts worth existing",
//            description = "UI concepts worth existing",
//            priority = Priority.HIGH,
//            reminderDateTime = null,
//            workerRequestId = null,
//            createdAt = Date(),
//            updatedAt = Date(),
//            categoryId = 1
//        ),
//        navigateToListScreen = { /* handle navigation */ },
//        navigateToChatbotScreen = { /* handle navigation */ },
//        navigateToImage2TextScreen = { /* handle navigation */ },
//        navigateToSpeech2TextScreen = { /* handle navigation */ },
//        sharedViewModel = SharedViewModel
//    )
//}
