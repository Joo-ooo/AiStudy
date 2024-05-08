package com.example.aistudy.ui.screens.Image2Text

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.compose.material.Button
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.aistudy.ui.viewmodels.SharedViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.aistudy.R
import com.example.aistudy.components.CustomText
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.ui.theme.BlackShade
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.primarySurface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import coil.compose.rememberImagePainter
import com.example.aistudy.ui.theme.Blue
import com.example.aistudy.ui.theme.GreyChat
import com.example.aistudy.ui.theme.fontFamily

    /**
     * This Screen encompasses a feature within an app that allows users to either select an image from their
     * device's gallery or capture a new photo using the device's camera. Upon selection or capture, the app performs
     * text recognition on the image and displays the recognized text on screen. The functionality is implemented
     * using Jetpack Compose for the UI, along with activity result contracts for handling image selection and
     * capturing processes. Additionally, it incorporates permission requests for camera access and utilizes
     * a shared ViewModel for state management across composables. The feature is structured into several key components:
     *
     * - Image2TextScreen: A composable function that serves as the main screen for the image to text conversion feature.
     *   It utilizes Scaffold for layout structure, with a top app bar, content area for displaying the selected image
     *   and recognized text, and a bottom bar for actions. The function also manages the dialog for choosing the image
     *   source and handles permission requests for camera usage.
     *
     * - toBitmap: A utility function that converts an image URI to a Bitmap object. This function supports both
     *   deprecated and current methods of image decoding, ensuring backward compatibility.
     *
     * - Image2TextBar: A composable function that defines the top app bar of the Image to Text screen. It includes a
     *   back navigation button and the screen title.
     *
     * - BackButton: A reusable composable function for rendering a back button with a customizable action.
     */

@SuppressLint("RestrictedApi")
@Composable
fun Image2TextScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    navigateToNoteScreen: (Int) -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf<String?>(null) }

    // Prepare a URI for the photo
    val photoFile = remember { sharedViewModel.createImageFile(context) }
    val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)

    // Launchers for picking image and taking picture
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
            uri?.let { sharedViewModel.recognizeTextFromImage(context, it) { text -> recognizedText = text } }
        }
    )

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageUri = photoUri
                sharedViewModel.recognizeTextFromImage(context, photoUri) { text -> recognizedText = text }
            }
        }
    )

    // Permission launcher for taking pictures
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                takePictureLauncher.launch(photoUri)
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = { Image2TextBar(onBackAction = { navController.popBackStack() }) },

        bottomBar = {
            Box(
                modifier = Modifier
                    .background(androidx.compose.material.MaterialTheme.colors.primary) // Set the background of the Box to be transparent
                    .fillMaxWidth()
                    .padding(bottom = 30.dp), // Adjust the padding to create space at the bottom)
                contentAlignment = Alignment.BottomCenter // Align the content (the Button) to the bottom center
            ) {
                // Define the Button
                Button(
                    onClick = {
                        showDialog = true
                    },
                    // Customize button colors
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    modifier = Modifier
                        .padding(10.dp) // Add bottom padding to the button
                        .clip(RoundedCornerShape(20)), // Make the button edges more rounded
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp) // Adjust the content padding inside the button
                ) {
                    // Define the content (Icon and Text) of the Button
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add a new photo",
                        color = BlackShade,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .background(androidx.compose.material.MaterialTheme.colors.primary)
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding()),) {
            Image(
                painter = rememberImagePainter(
                    data = R.drawable.ic_imagetotext,
                    builder = {
                        size(500)
                    }
                ),
                contentDescription = "Background",
                contentScale = ContentScale.Crop, // To center the image
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 80.dp),
                colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.3f)) // White with 30% opacity
            )
                LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                    // Your LazyColumn content here, excluding the buttons
                    imageUri?.let { uri ->
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Surface(
                                    color = Blue,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = uri),
                                        contentDescription = "Selected Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 100.dp, max = 300.dp)
                                            .padding(10.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.FillWidth,
                                    )
                                }
                            }
                        }
                    }
                    item {
                        recognizedText?.let { text ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Surface(
                                    color = GreyChat,
                                    shape = RoundedCornerShape(12.dp)
//                                elevation = 4.dp
                                ) {
                                    TextField(
                                        value = text,
                                        onValueChange = {}, // No action needed since it's read-only
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color.Transparent, // Use transparent to keep the Surface color
                                            disabledTextColor = Color.White, // Text color when the TextField is not editable
                                            disabledLabelColor = Color.Transparent, // Hide the label when the TextField is not editable
                                            focusedIndicatorColor = Color.Transparent, // Hide underline when focused
                                            unfocusedIndicatorColor = Color.Transparent, // Hide underline when not focused
                                            disabledIndicatorColor = Color.Transparent // Hide underline when disabled (read-only)
                                        ),
                                        readOnly = true, // Makes the text field non-editable
                                        textStyle = TextStyle(
                                            color = MaterialTheme.colors.secondary,
                                            fontFamily = fontFamily,
                                            fontWeight = FontWeight.W400,
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.Start // Align text to the start (left)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth() // Fill the available width
                                            .padding(8.dp) // Add padding as needed
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
//            title = {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Spacer(modifier = Modifier.width(8.dp))
//                    CustomText(text = "Add Photo", color = Color.White, fontWeight = FontWeight.W600, fontSize = 20.sp)
//                }
//            },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showDialog = false
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                takePictureLauncher.launch(photoUri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
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
}



@Composable
fun Image2TextBar(onBackAction: () -> Unit) {
    TopAppBar(
        elevation = 0.dp,
        navigationIcon = {
            Divider(modifier = Modifier.width(12.dp), color = BlackShade)
            BackButton(onBackAction) // The lambda here is directly usable
        },
        title = {
            CustomText(
                text = "Image To Text",
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600
            )
        },
        backgroundColor = BlackShade,
        actions = {
            Divider(modifier = Modifier.width(12.dp), color = BlackShade)
        }
    )
}

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

