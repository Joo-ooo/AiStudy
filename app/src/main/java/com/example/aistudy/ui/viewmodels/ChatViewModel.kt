package com.example.aistudy.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aistudy.data.models.Chat
import com.example.aistudy.data.repositories.ChatData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

    /**
     * ChatViewModel extends ViewModel and manages the UI state and logic of a chat interface. It processes UI events,
     * updates the chat state, and interacts with a chat data source for sending and receiving messages.
     *
     * Properties:
     * - _chatState: A private MutableStateFlow holding the current chat state, including the list of chat messages.
     * - chatState: A public, read-only StateFlow derived from _chatState to be observed by the UI for updates.
     * - _isBotTyping: A private MutableStateFlow indicating whether the bot is currently typing a response.
     * - isBotTyping: A public, read-only StateFlow derived from _isBotTyping for UI observation.
     *
     * Functions:
     * - onEvent(event: ChatUiEvent): Processes chat UI events, such as sending a prompt or updating the current prompt text.
     *   It differentiates actions based on the event type (SendPrompt or UpdatePrompt).
     * - addPrompt(prompt: String, bitmap: Bitmap?): Adds a new user message to the chat list and clears the current prompt and bitmap.
     *   It also sets the bot to a typing state.
     * - getResponse(prompt: String): Fetches a text response from the chat data source and adds it to the chat list.
     * - getResponseWithImage(prompt: String, bitmap: Bitmap): Similar to getResponse but for prompts that include an image.
     */

class ChatViewModel : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private val _isBotTyping = MutableStateFlow(false)
    val isBotTyping = _isBotTyping.asStateFlow()

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    addPrompt(event.prompt, event.bitmap)

                    if (event.bitmap != null) {
                        getResponseWithImage(event.prompt, event.bitmap)
                    } else {
                        getResponse(event.prompt)
                    }
                }
            }

            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    private fun addPrompt(prompt: String, bitmap: Bitmap?) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt, bitmap, true))
                },
                prompt = "",
                bitmap = null
            )
        }
        // Set the bot to typing state when a prompt is added
        _isBotTyping.value = true
    }

    private fun getResponse(prompt: String) {
        viewModelScope.launch {
            val chat = ChatData.getResponse(prompt)
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }
            _isBotTyping.value = false
        }
    }

    private fun getResponseWithImage(prompt: String, bitmap: Bitmap) {
        viewModelScope.launch {
            val chat = ChatData.getResponseWithImage(prompt, bitmap)
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }
            _isBotTyping.value = false
        }
    }
}

















