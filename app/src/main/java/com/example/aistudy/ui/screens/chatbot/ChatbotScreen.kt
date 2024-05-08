package com.example.aistudy.ui.screens.chatbot

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.aistudy.ui.viewmodels.SharedViewModel
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.aistudy.R
import com.example.aistudy.components.chat.ChatScreen
import com.example.aistudy.components.CustomText
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.ui.theme.BlackShade
import com.example.aistudy.ui.theme.Blue
import com.example.aistudy.ui.theme.GeminiChatBotTheme
import com.example.aistudy.ui.theme.OnlineGreen
import com.example.aistudy.ui.viewmodels.ChatViewModel
import java.io.IOException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/** ChatbotScreen is a composable function that sets up the UI for a chatbot feature within an app.
 * It manages user interactions, image picking, and displays messages between the user and the bot.
 * */
@Composable
fun ChatbotScreen(
    navigateToNoteScreen: (Int) -> Unit,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val activity = context as Activity
    val keyboardController = LocalSoftwareKeyboardController.current

    // Observing lifecycle to handle configuration changes like rotation
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        lifecycle.addObserver(observer)

        // Cleanup function to be called when the composable leaves the composition
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }


    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    val chatViewModel = remember { ChatViewModel() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageUriState.value = uri
    }

    // Convert the URI to a Bitmap
    val bitmap = imageUriState.value?.let { uri ->
        context.toBitmap(uri)
    }

    GeminiChatBotTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BlackShade
        ) {
            val isBotTyping = chatViewModel.isBotTyping.collectAsState()
            androidx.compose.material.Scaffold(
                backgroundColor = androidx.compose.material.MaterialTheme.colors.primary,
                topBar = { ChatbotBar(onBackAction = { navController.popBackStack() }, isBotTyping = isBotTyping) },        content = { paddingValues ->
                    ChatScreen(
                        paddingValues = paddingValues,
                        chatViewModel = chatViewModel,
                        imagePickerLauncher = { request ->
                            imagePickerLauncher.launch(request)
                        },
                        bitmap = bitmap,
                        onImageSent = {
                            imageUriState.value = null // Clear the image URI
                            keyboardController?.hide()
                        }
                    )
                }
            )
        }
    }
}

// Helper function to convert a URI to Bitmap
fun Context.toBitmap(uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(this.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    } catch (e: IOException) {
        null
    }
}

// Composable function for displaying the top app bar with custom behavior for the chatbot screen.
@Composable
fun ChatbotBar(onBackAction: () -> Unit, isBotTyping: State<Boolean>) {

    var typingText by remember { mutableStateOf("online") }

    // Animate text change when bot is typing
    LaunchedEffect(isBotTyping.value) {
        if (isBotTyping.value) {
            var dots = 0
            while (isBotTyping.value) {
                typingText = "typing" + ".".repeat(dots)
                delay(600)  // Adjust speed as needed
                dots = (dots + 1) % 4  // Cycle through 0 to 3 dots
            }
        } else {
            typingText = "online"
        }
    }

    TopAppBar(
        elevation = 0.dp,
        modifier = Modifier.padding(top = 10.dp, bottom = 5.dp),
        navigationIcon = {
            Divider(modifier = Modifier.width(12.dp), color = BlackShade)
            BackButton(onBackAction) // Invokes onBackAction when clicked
        },
        title = {
            // Column for stacking the text elements
            Column{
                CustomText(
                    text = "Marcus",
                    color = androidx.compose.material.MaterialTheme.colors.secondary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600)
                CustomText(
                    text = typingText,
                    color = if (isBotTyping.value) Blue else OnlineGreen,
                    fontWeight = FontWeight.W400,
                    fontSize = 18.sp)
            }
        },
        backgroundColor = BlackShade,
        actions = {
            Divider(modifier = Modifier.width(12.dp), color = BlackShade)
        }
    )
}

// Rendering a customizable back button.
@Composable
fun BackButton(backButtonPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable(onClick = backButtonPressed), // Invoke the passed lambda on click
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(id = R.string.back_arrow),
            tint = Color.White
        )
    }
}