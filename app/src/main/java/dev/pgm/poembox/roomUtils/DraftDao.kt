package dev.pgm.poembox.roomUtils

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

    @Update
    suspend fun updateNote(draft: Draft)

    @Delete
    suspend fun deleteNote(draft: Draft)

}