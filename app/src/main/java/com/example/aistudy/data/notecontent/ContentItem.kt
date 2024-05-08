package com.example.aistudy.data.notecontent

import kotlinx.serialization.*

/**
 * This sealed class hierarchy represents the different types of content items supported within a multi-modal note.
 * It defines the structure for:
 *
 * * **TextContent:**  Stores plain text content
 * * **TranscriptContent:** Holds a reference to a transcript (by ID and title)
 * * **PhotoContent:**  Stores a reference to an image (by URI)
 *
 * Note: These content items are stored or transmitted in JSON format.
 */

@Serializable
sealed class ContentItem {
    @Serializable
    @SerialName("text")
    data class TextContent(val text: String) : ContentItem()

    @Serializable
    @SerialName("transcript")
    data class TranscriptContent(val id: Int, val title: String) : ContentItem()

    @Serializable
    @SerialName("photo")
    data class PhotoContent(val uri: String) : ContentItem()
}

