package com.example.aistudy.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aistudy.utils.Constants.DATABASE_TABLE
import java.util.*

/**
 * This data class represents a single note within the application.
 * This is is used for Room database persistence.
 */
@Entity(tableName = DATABASE_TABLE)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val contentItemsJson: String = "", // Storing JSON for multi modal description
    val categoryId: Int,
    val reminderDateTime: Date?,
    val workerRequestId: UUID?, // Optional UUID for associating with background tasks (notification reminders)
    val createdAt: Date,
    val updatedAt: Date,
)


