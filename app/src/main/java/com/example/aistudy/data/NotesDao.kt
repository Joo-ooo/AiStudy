package com.example.aistudy.data

import androidx.paging.PagingSource
import androidx.room.*
import com.example.aistudy.data.models.Note
import com.example.aistudy.utils.Constants.DATABASE_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM $DATABASE_TABLE ORDER BY id ASC")
    fun getAllNotes(): PagingSource<Int, Note>

    @Query("SELECT * FROM $DATABASE_TABLE WHERE id=:noteId")
    fun getSelectedNote(noteId: Int): Flow<Note>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM $DATABASE_TABLE")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM $DATABASE_TABLE WHERE title LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchNotes(searchQuery: String): PagingSource<Int, Note>

    @Query("SELECT * FROM $DATABASE_TABLE WHERE categoryId = :categoryId")
    fun filterByCategory(categoryId: Int): PagingSource<Int, Note>

}
