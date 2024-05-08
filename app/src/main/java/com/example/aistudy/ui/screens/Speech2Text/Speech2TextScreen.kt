package com.example.aistudy.ui.screens.Speech2TextScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aistudy.R
import com.example.aistudy.components.CustomText
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.ui.theme.BlackShade
import com.example.aistudy.ui.theme.Blue
import com.example.aistudy.ui.theme.ChineseSilver
import com.example.aistudy.ui.theme.White
import com.example.aistudy.ui.theme.fontFamily
import com.example.aistudy.ui.viewmodels.SharedViewModel

    /**
     * The Speech2TextScreen composable function creates a UI for displaying and editing the transcript of audio recordings.
     * It integrates various functionalities including updating transcript titles, navigating through the app, and playing audio
     * with controls for play, pause, skip, and seek. It leverages a shared ViewModel for state management and data fetching.
     *
     * Key Components:
     * - Scaffold structure with a custom top app bar for navigation and title editing, content area for the transcript text,
     *   and a bottom bar with audio playback controls.
     * - LaunchedEffect for loading the transcript data based on the transcriptId.
     * - State management for UI elements like playback status and transcript text through sharedViewModel.
     * - BackHandler for navigation handling, ensuring users can navigate back properly from the screen.
     * - A custom AppBar (TranscriptBar) that allows for title editing directly within the app bar.
     * - TranscriptContent for displaying the parsed transcript content in a scrollable view.
     * - AudioPlayer providing a UI for audio control, including play/pause, seek, and skip functionalities, tied to the sharedViewModel for logic handling.
     *
     * Functionality:
     * - Upon entering the screen, it immediately fetches and displays the selected transcript's details.
     * - Users can edit the title of the transcript directly on the top app bar.
     * - The transcript text is displayed in a scrollable column format, parsed from an SRT format.
     * - The audio playback controls at the bottom allow the user to control the playback of the associated audio recording,
     *   including seeking to specific parts of the audio and skipping forwards or backwards.
     */

@Composable
fun Speech2TextScreen(
    transcriptId: Int,
    navigateToNoteScreen: (Int) -> Unit,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {

    // Load the transcript details when the screen is first composed
    LaunchedEffect(transcriptId) {
        sharedViewModel.loadTranscriptById(transcriptId)
    }

    // Observe changes in title and transcript text
    val title by sharedViewModel.TranscriptTitle
    val transcriptText by sharedViewModel.TranscriptText.collectAsState()

    var isPlaying by remember { mutableStateOf(false) }
    val currentPosition by remember { mutableStateOf(0) } // Replace with actual logic to get the current position
    val totalDuration by remember { mutableStateOf(0) } // Replace with actual logic to get the duration

    androidx.compose.material.Scaffold(backgroundColor = androidx.compose.material.MaterialTheme.colors.primary,
        topBar = { TranscriptBar(
            onBackAction = { navController.popBackStack() },
            title = title,
            onTitleChange = { newTitle ->
                sharedViewModel.updateTranscriptTitle(transcriptId, newTitle)
            }) },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)
            ) {
                TranscriptContent(transcriptText = transcriptText)
            }
        },
        bottomBar = {
            AudioPlayer(
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                totalDuration = totalDuration,
                sharedViewModel = sharedViewModel,
                onPlayPauseClicked = {
                    sharedViewModel.playPauseAudio()
                    isPlaying = !isPlaying
                },
                onSeek = { progress ->
                    sharedViewModel.seekAudio(progress)
                },
                onSkipPrevious = {
                    sharedViewModel.skipBackward()
                },
                onSkipNext = {
                    sharedViewModel.skipForward()
                }
            )
        }
    )
}

@Composable
fun TranscriptBar(
    onBackAction: () -> Unit,
    title: String,
    onTitleChange: (String) -> Unit) {
    androidx.compose.material.TopAppBar(
        elevation = 0.dp,
        navigationIcon = {
            androidx.compose.material.Divider(modifier = Modifier.width(12.dp), color = BlackShade)
            BackButton(onBackAction)
        },
        title = {
            androidx.compose.material.TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = title,
                onValueChange = { text ->
                    if (text.length <= 35) onTitleChange(text)
                },
                placeholder = {
                    CustomText(
                        modifier = Modifier.alpha(ContentAlpha.medium),
                        text = stringResource(id = R.string.enter_title),
                        color = ChineseSilver,
                        fontWeight = FontWeight.W300,
                        fontSize = 18.sp
                    )
                },
                textStyle = TextStyle(
                    color = androidx.compose.material.MaterialTheme.colors.secondary,
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
        },
        backgroundColor = BlackShade,
        actions = {
            androidx.compose.material.Divider(modifier = Modifier.width(12.dp), color = BlackShade)
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
        androidx.compose.material.Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(id = R.string.back_arrow),
            tint = White
        )
    }
}

@Composable
fun TranscriptContent(transcriptText: String) {
    val transcripts = parseSrtTranscript(transcriptText)

    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 5.dp)
        .verticalScroll(scrollState)) {
        transcripts.forEach { entry ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 8.dp)
            ) {
                Text(
                    text = entry.text,
                    style = TextStyle(
                        color = androidx.compose.material.MaterialTheme.colors.secondary,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.W400,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = entry.startTime,
                        style = TextStyle(
                            color = ChineseSilver,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.W300,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.align(Alignment.Bottom)
                    )
                }
            }
        }
    }
}


// Example data class for transcript entries
data class TranscriptEntry(
    val startTime: String,
    val endTime: String,
    val text: String
)

@Composable
fun AudioPlayer(
    isPlaying: Boolean,
    currentPosition: Int,
    totalDuration: Int,
    onPlayPauseClicked: () -> Unit,
    onSeek: (Float) -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    // Observing StateFlow values
    val currentPosition by sharedViewModel.currentPosition.collectAsState()
    val totalDuration by sharedViewModel.totalDuration.collectAsState()

    val sliderPosition = if (totalDuration > 0) currentPosition.toFloat() / totalDuration else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { newValue ->
                val newProgress = (newValue * totalDuration).toInt()
                sharedViewModel.seekAudio(newValue) // Call ViewModel's seek function
            },
            modifier = Modifier.fillMaxWidth().padding(start = 23.dp, end = 23.dp),
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Blue)
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
        ) {
            Text(
                text = formatTime(currentPosition),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 25.dp)
                    .align(alignment = Alignment.Top),
                style = TextStyle(
                    color = ChineseSilver,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W300,
                    fontSize = 14.sp)
            )

            IconButton(onClick = onSkipPrevious) {
                Icon(
                    imageVector = Icons.Default.Replay10,
                    contentDescription = "Skip back 10 seconds",
                    modifier = Modifier
                        .size(24.dp),
                    tint = White
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) Blue else Color.Gray)
                    .clickable { onPlayPauseClicked() }
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = onSkipNext) {
                Icon(
                    imageVector = Icons.Default.Forward10,
                    contentDescription = "Skip forward 10 seconds",
                    modifier = Modifier.size(24.dp),
                    tint = White
                )
            }

            Text(
                text = formatTime(totalDuration),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 25.dp)
                    .align(alignment = Alignment.Top),
                textAlign = TextAlign.End,
                style = TextStyle(
                    color = ChineseSilver,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W300,
                    fontSize = 14.sp)
            )
        }
    }
}


// Helper function to format the time
private fun formatTime(milliseconds: Int): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

//@Composable
//@Preview
//fun AudioPlayerPreview() {
//    AudioPlayer(
//        isPlaying = true,
//        currentPosition = 0,
//        totalDuration = 100,
//        onPlayPauseClicked = {},
//        onSeek = {},
//        onSkipPrevious = {},
//        onSkipNext = {}
//        )
//}

fun parseSrtTranscript(transcript: String): List<TranscriptEntry> {
    val regex = """\d+\s+(\d{2}:\d{2}:\d{2},\d{3}) --> (\d{2}:\d{2}:\d{2},\d{3})\s+(.*?)\s*(?=\d+|$)""".toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE))
    return regex.findAll(transcript).map { matchResult ->
        val (startTime, endTime, text) = matchResult.destructured
        TranscriptEntry(startTime, endTime, text.replace("\n", " "))
    }.toList()
}


