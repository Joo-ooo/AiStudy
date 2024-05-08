package com.example.aistudy.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.aistudy.data.NotesDao
import com.example.aistudy.data.models.Note
import com.example.aistudy.utils.Constants
import com.example.aistudy.utils.Constants.NOTE_PAGE_SIZE
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

    /**
     * Repository class for managing note operations. It provides a layer of abstraction over the
     * NotesDao, facilitating interactions with the database for CRUD operations on notes. This class
     * leverages the Paging library to handle large datasets efficiently and supports searching and
     * filtering notes, ensuring scalable and responsive data handling within the app.
     */
@ViewModelScoped
class NotesRepository @Inject constructor(private val notesDao: NotesDao) {

    // Retrieves all the notes in a paginated format
    // And returns a flow of PagingData containing Note objects
    fun getAllNotes(): Flow<PagingData<Note>> {
        val pagingSourceFactory = { notesDao.getAllNotes() }
        return Pager(
            PagingConfig(
                pageSize = NOTE_PAGE_SIZE // Number of items to load at once
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    // Fetch nodes by ID
    fun getSelectedNote(noteId: Int): Flow<Note> {
        return notesDao.getSelectedNote(noteId = noteId)
    }

    suspend fun addNote(note: Note) {
        notesDao.addNote(note = note);
    }

    suspend fun updateNote(note: Note) {
        notesDao.updateNote(note = note);
    }

    suspend fun deleteNote(note: Note) {
        notesDao.deleteNote(note = note);
    }

    suspend fun deleteAllNotes() {
        notesDao.deleteAllNotes();
    }

    // Search notes based on a query string and returns results in pagniated format
    fun searchNotes(searchQuery: String): Flow<PagingData<Note>> {
        val pagingSourceFactory = { notesDao.searchNotes(searchQuery = searchQuery) }
        return Pager(
            PagingConfig(
                pageSize = NOTE_PAGE_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    // Filter notes based on categoryId and return results in paginated foramt
    fun filterNotesByCategory(categoryId: Int): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.NOTE_PAGE_SIZE),
            pagingSourceFactory = { notesDao.filterByCategory(categoryId) }
        ).flow
    }
}