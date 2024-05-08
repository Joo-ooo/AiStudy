package com.example.aistudy.data.repositories

import android.graphics.Bitmap
import com.example.aistudy.data.models.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

    /**
    * Object that has functions to interact with Gemini AI model to produce chat responses from
    * both text and image.
     */


object ChatData {

    val api_key = "AIzaSyCNKorEbJU6DX89Zh5DAz5JzL5IYQ7TKz4"

    // A suspend function to fetch a response from a generative model based on the given text prompt.
    suspend fun getResponse(prompt: String): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro", apiKey = api_key
        )

        try {
            // Asynchronously requests content generation in an I/O-optimized dispatcher, returning the response.
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            // Returns error messsage if response is empty
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null, // Bitmap is set to null since this response does not include an image
                isFromUser = false // Indicates the message is from the generative model, not the user
            )

        } catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }

    }

    // Function to fetch a response from a generative model that includes image processing, based on the given text prompt and bitmap.
    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision", apiKey = api_key
        )

        try {
            // Prepares the content input for the model, including both text and image
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }

            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )

        } catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }

    }

}