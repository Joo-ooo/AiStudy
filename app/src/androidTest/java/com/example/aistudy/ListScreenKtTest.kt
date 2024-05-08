package com.example.aistudy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.compose.LazyPagingItems
import com.example.aistudy.data.models.Note
import com.example.aistudy.ui.screens.list.FloatingButton
import com.example.aistudy.ui.screens.list.HandleListContent
import com.example.aistudy.utils.Action
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test

    /**
     * ListScreenKtTest is a test class designed to verify the behavior of UI components within the ListScreen, specifically focusing on the FloatingButton composable function.
     *
     * Test Methods:
     * - floatingButton(): This test case checks if the FloatingButton composable reacts to user interactions as expected,
     * specifically testing the button's click functionality.
     *   - A local variable, wasClicked, is defined to track if the button's click action is executed.
     *   - The setContent method provided by composeTestRule is used to load the FloatingButton composable into the testing environment.
     *   An onClick action is provided to the FloatingButton, which updates the wasClicked variable if the button is clicked with the expected taskId.
     *   - The test finds the FloatingButton using onNodeWithContentDescription, targeting the button's content description ("Add Note").
     *   The useUnmergedTree parameter is set to true to search within all nodes, including those that might be part of the merged semantics tree.
     *   - performClick() simulates a click action on the floating action button.
     *   - An assertion checks if the wasClicked variable is true, indicating that the FloatingButton's onClick action was triggered successfully by the simulated click.
     */

class ListScreenKtTest {

    @get:Rule
    // Setting up of testing environment to load up composable functions
    val composeTestRule = createComposeRule()

    @Test
    fun floatingButton() {
        var wasClicked = false
        composeTestRule.setContent {
            // Simulates that button has been successfully interacted with
            FloatingButton(onFloatingActionButtonPressed = { taskId ->
                if (taskId == -1) {
                    wasClicked = true
                }
            })
        }

        composeTestRule
            // Finds the floating button UI based content description
            .onNodeWithContentDescription("Add Note", useUnmergedTree = true)
            .performClick()

        assert(wasClicked) // Checks if action defined for floating button (click) has been executed
    }
}