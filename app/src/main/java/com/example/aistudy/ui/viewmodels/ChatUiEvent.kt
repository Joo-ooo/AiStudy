package com.example.aistudy.ui.viewmodels

import android.graphics.Bitmap

    /**
     * This class serves as a hierarchical type to represent
     * different types of actions or events that can occur within the chat interface, enabling type-safe handling
     * of these events in the UI or the ViewModel.
     */

sealed class ChatUiEvent {
    data class UpdatePrompt(val newPrompt: String) : ChatUiEvent()
    data class SendPrompt(
        val prompt: String,
        val bitmap: Bitmap?
    ) : ChatUiEvent()
}