package dev.pgm.poembox.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {

    @Query("SELECT * FROM drafts WHERE id=:id")
    suspend fun findById(id: Int): Draft

    @Query("SELECT * FROM drafts WHERE title=:title")
    suspend fun findByTitle(title: String): Draft

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDraft(draft: Draft)

    @Query("SELECT * FROM drafts ORDER BY writtenDate DESC")
    fun getOrderDrafts(): Flow<List<Draft>>

    @Query("SELECT * from drafts")
    suspend fun getAllDrafts(): MutableList<Draft>

    @Query("UPDATE drafts SET draftAnnotation=:notes WHERE title=:title")
    suspend fun updateNoteByTitle(notes: String, title: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDraft(draft: Draft)

    @Delete
    suspend fun deleteDraft(draft: Draft)

}