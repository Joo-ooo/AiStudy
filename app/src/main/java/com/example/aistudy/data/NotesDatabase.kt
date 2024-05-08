package com.example.aistudy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.aistudy.data.models.Note
import com.example.aistudy.utils.Converters
import com.example.aistudy.data.models.Category
import com.example.aistudy.data.models.Transcript

@Database(entities = [Note::class, Category::class, Transcript::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transcriptDao(): TranscriptDao
}