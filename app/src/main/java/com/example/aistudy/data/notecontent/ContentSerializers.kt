package com.example.aistudy.data.notecontent

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * This object configures Kotlinx Serialization for handling the ContentItems.
 * * Registers polymorphic serializers to handle serialization and deserialization of the
 * * different `ContentItem` subclasses (`TextContent`, `TranscriptContent`, `PhotoContent`)
 */
object ContentSerializers {
    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(ContentItem::class) {
                subclass(ContentItem.TextContent::class, ContentItem.TextContent.serializer())
                subclass(ContentItem.TranscriptContent::class, ContentItem.TranscriptContent.serializer())
                subclass(ContentItem.PhotoContent::class, ContentItem.PhotoContent.serializer())
            }
        }
        // Enable pretty printing if desired
        prettyPrint = true
    }
}
