package com.example.aistudy.di

import android.content.Context
import androidx.room.Room
import com.example.aistudy.data.NotesDatabase
import com.example.aistudy.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

    /**
     * AppModule is a Dagger-Hilt module that provides singleton-scoped dependencies for the application.
     * It is responsible for creating and providing instances of the NotesDatabase, as well as DAOs for
     * accessing various entities such as notes, categories, and transcripts. This module ensures that
     * these components are available application-wide, supporting a structured and efficient approach
     * to dependency injection and data management.
     */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNotesDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, NotesDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideNotesDao(database: NotesDatabase) = database.notesDao()

    @Singleton
    @Provides
    fun provideCategoryDao(database: NotesDatabase) = database.categoryDao()

    @Singleton
    @Provides
    fun provideTranscriptDao(database: NotesDatabase) = database.transcriptDao()
}