package com.example.aistudy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aistudy.data.models.Transcript
import com.example.aistudy.utils.Constants.TSC_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTranscript(transcript: Transcript): Long

    @Query("SELECT * FROM $TSC_TABLE WHERE id = :id")
    suspend fun getTranscriptById(id: Int): Transcript?

    @Query("UPDATE $TSC_TABLE SET name = :newTitle WHERE id = :transcriptId")
    suspend fun updateTitleById(transcriptId: Int, newTitle: String)

    @Query("SELECT name FROM transcript_table WHERE id = :id")
    fun getTranscriptTitleFlowById(id: Int): Flow<String>

}