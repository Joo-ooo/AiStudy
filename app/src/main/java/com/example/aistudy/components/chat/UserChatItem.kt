package com.example.aistudy.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aistudy.ui.theme.BlueChat
import com.example.aistudy.ui.theme.ChineseSilver
import com.example.aistudy.ui.theme.fontFamily
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * This composable represents a single chat message from the User. It is responsible for:
 *
 * * Styling the message background with a rounded rectangular shape and appropriate color
 * * Displaying the user's message text
 * * Showing a timestamp for the message
 */
@Composable
fun UserChatItem(prompt: String, bitmap: Bitmap?) {

    // Formatter for displaying the message time in a short format
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    // Container for the chat bubble and timestamp
    Column(
        modifier = Modifier.padding(start = 100.dp, bottom = 16.dp)
    ) {

        // If image exists, show image in message
        bitmap?.let {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(bottom = 2.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    ),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                bitmap = it.asImageBitmap()
            )
        }

        // Chat bubble with the message
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 0.dp // Sharp corner
                ))
                .background(BlueChat)
                .padding(16.dp),
            text = prompt,
            fontSize = 20.sp,
            color = Color.White
        )

        // Timestamp of the message
        Text(
            text = LocalTime.now().format(timeFormatter),
            style = TextStyle(
                color = ChineseSilver,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W300,
                fontSize = 14.sp
            ),
            modifier = Modifier
                .align(Alignment.End) // Align timestamp to bottom right of msg
                .padding(top = 4.dp, end = 16.dp)
        )

    }
}
