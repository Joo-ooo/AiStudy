package com.example.aistudy.utils

import androidx.room.TypeConverter
import com.example.aistudy.data.notecontent.ContentItem
import com.example.aistudy.data.notecontent.ContentSerializers
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.serialization.encodeToString
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun uuidFromString(string: String?): UUID? {
        return string?.let { UUID.fromString(string) }
    }

    class ContentItemConverter {
        @TypeConverter
        fun fromContentItemList(value: List<ContentItem>?): String =
            ContentSerializers.json.encodeToString(value)

        @TypeConverter
        fun toContentItemList(value: String): List<ContentItem>? =
            ContentSerializers.json.decodeFromString(value)
    }

}
