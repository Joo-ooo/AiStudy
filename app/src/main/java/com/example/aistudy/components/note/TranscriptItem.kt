package com.example.aistudy.components.note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aistudy.ui.theme.Blue
import com.example.aistudy.ui.viewmodels.SharedViewModel

/**
 * This composable represents a single transcript item that is an entry point for transcript screen.
 *
 * It has the following:
 * * The title of the generated transcript
 * * Buttons to go to or delete the corresponding transcript
 * * Dynamic fetching of the transcript title based on its ID
 */
@Composable
fun TranscriptItem(
    transcriptId: Int,
    navigateToSpeech2TextScreen: (Int) -> Unit,
    onDelete: () -> Unit,
    SharedViewModel: SharedViewModel
) {
    val title by SharedViewModel.getLiveTranscriptTitleById(transcriptId).collectAsState(initial = "Loading...")

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier.padding(start = 0.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // delete transcript
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Ensure the Column for the title takes up the remaining space
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp) // Optional padding for spacing between delete icon and title
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            // IconButton for navigating to the transcript remains on the right
            IconButton(
                onClick = {
                    navigateToSpeech2TextScreen(transcriptId)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Play",
                    tint = Blue,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
