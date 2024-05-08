package com.example.aistudy.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aistudy.utils.Constants
import com.example.aistudy.utils.Constants.TSC_TABLE

/**
 * This data class represents a single transcript and is used for Room database persistence.
 * A new row is added when a new transcript is added in any note.
 */
@Entity(tableName = TSC_TABLE)
data class Transcript(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val transcript: String, // Transcript generated from audio
    val filepath: String // Filepath of audio uploaded
)
