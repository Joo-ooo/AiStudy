package com.example.aistudy.data.models

import android.graphics.Bitmap

/**
 * This data class defines the structure of a single chat message exchanged between the user and the chatbot.
 */
data class Chat (
    val prompt: String,
    val bitmap: Bitmap?,
    val isFromUser: Boolean
)
