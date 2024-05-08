package com.example.aistudy.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aistudy.ui.theme.ChineseSilver
import com.example.aistudy.ui.theme.GreyChat
import com.example.aistudy.ui.theme.fontFamily
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * This composable represents a single chat message from the Gemini bot. It is responsible for:
 *
 * * Styling the message background with a rounded rectangular shape and appropriate color
 * * Displaying the chatbot's response text
 * * Showing a timestamp for the message
 */
@Composable
fun ModelChatItem(response: String) {

    // Formatter for displaying the message time in a short format
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    // Container for the chat bubble and timestamp
    Column(
        modifier = Modifier.padding(end = 100.dp, bottom = 16.dp)
    ) {
        // Chat bubble with the message
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 0.dp, // Sharp corner
                        bottomEnd = 12.dp
                ))
                .background(GreyChat)
                .padding(16.dp),
            text = response,
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
                .align(Alignment.Start) // Align timestamp to bottom left of msg
                .padding(top = 5.dp, start = 14.dp)
        )

    }
}
