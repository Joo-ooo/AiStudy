package com.example.aistudy.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.graphics.Bitmap
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.aistudy.R
import com.example.aistudy.components.CustomText
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.ui.theme.Blue
import com.example.aistudy.ui.theme.White
import com.example.aistudy.ui.theme.fontFamily
import com.example.aistudy.ui.viewmodels.ChatUiEvent
import com.example.aistudy.ui.viewmodels.ChatViewModel

/**
 * This composable represents the primary Chatbot interaction screen where users can exchange messages with the chatbot.
 *
 * It is responsible for:
 * * Displaying a scrollable chat history with distinct styling for user and chatbot messages (`UserChatItem`, `ModelChatItem`)
 * * Managing the text input field and send functionality for user prompts
 * * Providing an option to attach images to user messages
 * * Coordinating with `ChatViewModel` to handle chat logic and state
 */
@Composable
fun ChatScreen(
    paddingValues: PaddingValues,
    chatViewModel: ChatViewModel,
    imagePickerLauncher: (PickVisualMediaRequest) -> Unit,
    bitmap: Bitmap?,
    onImageSent: () -> Unit
) {

    // Collect the latest chat state from the ViewModel
    val chatState = chatViewModel.chatState.collectAsState().value
    // Wrapping content in a Box to add the background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        Image(
            painter = rememberImagePainter(
                data = R.drawable.ic_chatbot,// Background image resource
                builder = {
                    size(500)
                }
            ),
            contentDescription = "Background",
            contentScale = ContentScale.Crop, // To center the image
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 80.dp),
            colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.3f)) // Apply a white tint for aesthetics
        )

        // Container for chat messages and input field
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom
        ) {
            // LazyColumn for displaying chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true // Newest messages at the bottom
            ) {
                itemsIndexed(chatState.chatList) { index, chat ->
                    if (chat.isFromUser) {
                        // Display user message
                        UserChatItem(
                            prompt = chat.prompt, bitmap = chat.bitmap
                        )
                    } else {
                        // Display bot response
                        ModelChatItem(response = chat.prompt)
                    }
                }
            }

            // Input field and send button
            Box(
                modifier = Modifier
                    .padding(bottom = 15.dp, start = 10.dp, end = 10.dp)
                    .clip(RoundedCornerShape(20))
                    .background(White)
                    .clickable {
                        // Trigger sending message
                        chatViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap))
                        onImageSent()
                    }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        // Attachment icon
                        Icon(
                            modifier = Modifier
                                .size(35.dp)
                                .clickable {
                                    imagePickerLauncher(
                                        PickVisualMediaRequest.Builder()
                                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            .build()
                                    )
                                },
                            painter = painterResource(id = R.drawable.ic_attach),
                            contentDescription = "Add Photo",

                            )
                        // Display the selected image thumbnail
                        bitmap?.let {
                            Image(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(bottom = 2.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentDescription = "picked image",
                                contentScale = ContentScale.Crop,
                                bitmap = it.asImageBitmap()
                            )
                        }
                    }

                    // Message input field
                    androidx.compose.material.TextField(
                        modifier = Modifier.weight(1f),
                        value = chatState.prompt,
                        onValueChange = {
                            chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                        },
                        placeholder = {
                            CustomText(
                                modifier = Modifier.alpha(ContentAlpha.medium),
                                text = "Ask me about Quiz 3 results",
                                color = BlackOlive,
                                fontWeight = FontWeight.W500,
                                fontSize = 18.sp
                            )
                        },
                        textStyle = TextStyle(
                            color = BlackOlive,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.W400,
                            fontSize = 18.sp
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = androidx.compose.material.MaterialTheme.colors.secondary,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            backgroundColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30))
                            .background(Blue)
                            .clickable {
                                chatViewModel.onEvent(
                                    ChatUiEvent.SendPrompt(
                                        chatState.prompt,
                                        bitmap
                                    )
                                )
                                onImageSent()
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sendbutton),
                            contentDescription = "Send prompt",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))

                }

            }

        }
    }
}
