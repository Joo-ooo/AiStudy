package com.example.aistudy.ui.viewmodels

import android.graphics.Bitmap
import com.example.aistudy.data.models.Chat

    /**
     * Data class representing the state of a chat interface within an application.
     * It holds the current list of chat messages, the current user input (prompt), and an optional Bitmap object
     * which can be used for image messages.
     */

data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)