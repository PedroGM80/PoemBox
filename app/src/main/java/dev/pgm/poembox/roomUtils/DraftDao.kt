package dev.pgm.poembox.roomUtils

import androidx.room.*
import dev.pgm.poembox.roomUtils.Draft
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {

    @Query("SELECT * FROM drafts WHERE title =:id")
    suspend fun findById(id: Int): Draft

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