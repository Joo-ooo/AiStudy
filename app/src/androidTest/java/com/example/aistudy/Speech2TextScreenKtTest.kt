package com.example.aistudy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.aistudy.ui.screens.Speech2TextScreen.BackButton
import com.example.aistudy.ui.screens.Speech2TextScreen.TranscriptContent
import com.example.aistudy.ui.screens.Speech2TextScreen.parseSrtTranscript
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test

    /**
     * Speech2TextScreenKtTest is a test suite designed to verify the functionality and behavior of components within
     * the Speech to Text screen of an Android application, particularly focusing on back button functionality, displaying
     * transcript content, and parsing SRT (SubRip Text) formatted transcripts.
     *
     * The class utilizes JUnit for structuring tests and the AndroidX Compose Test Rule for interacting with Compose UI elements.
     *
     * Test Cases:
     * 1. backButton():
     *    - Tests the BackButton Composable's click action. It verifies that when the back button is clicked, the assigned action is triggered.
     *    - A variable backButtonPressed is set to false initially and is meant to be updated to true upon clicking the back button.
     *    - The Compose Test Rule sets the UI content to render the BackButton, and simulates a click action.
     *    - Assertions check if backButtonPressed becomes true after the click, indicating the click action was successfully detected and handled.
     *
     * 2. transcriptContent():
     *    - Verifies that TranscriptContent correctly displays text from a provided SRT transcript.
     *    - It sets the content to display a sample SRT transcript and then asserts that specific text lines ("Hello World" and "Second Line") are visible in the UI, ensuring the transcript content is parsed and displayed as expected.
     *
     * 3. parseSrtTranscript():
     *    - Directly tests the parseSrtTranscript function's ability to correctly parse an SRT formatted string into structured data.
     *    - Supplies a sample SRT string and invokes parseSrtTranscript, then performs several assertions to check that:
     *      a) The correct number of transcript entries are parsed.
     *      b) Each entry's start time, end time, and text content match expected values.
     */

class Speech2TextScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun backButton() {
        var backButtonPressed = false
        composeTestRule.setContent {
            BackButton { backButtonPressed = true }
        }

        composeTestRule
            .onNodeWithContentDescription("Back Arrow") // Adjust if using a different content description
            .performClick()

        assert(backButtonPressed)
    }
    @Test
    fun transcriptContent() {
        val testTranscript = "1\n00:00:01,000 --> 00:00:02,000\nHello World\n\n2\n00:00:03,000 --> 00:00:04,000\nSecond Line"
        composeTestRule.setContent {
            TranscriptContent(testTranscript)
        }

        composeTestRule.onNodeWithText("Hello World").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second Line").assertIsDisplayed()
    }
    @Test
    fun parseSrtTranscript() {
        val srt = "1\n00:00:01,000 --> 00:00:02,000\nHello World\n\n2\n00:00:03,000 --> 00:00:04,000\nSecond Line"
        val result = parseSrtTranscript(srt)

        assertEquals(2, result.size)
        assertEquals("00:00:01,000", result[0].startTime)
        assertEquals("00:00:02,000", result[0].endTime)
        assertEquals("Hello World", result[0].text)
        assertEquals("00:00:03,000", result[1].startTime)
        assertEquals("00:00:04,000", result[1].endTime)
        assertEquals("Second Line", result[1].text)
    }
}

