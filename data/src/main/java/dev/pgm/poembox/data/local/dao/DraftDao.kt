package dev.pgm.poembox.data.local.dao

import androidx.room.*
import dev.pgm.poembox.data.local.entities.DraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {

    @Query("SELECT * FROM drafts WHERE id=:id")
    suspend fun findById(id: Int): DraftEntity?

    @Query("SELECT * FROM drafts WHERE title=:title")
    suspend fun findByTitle(title: String): DraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDraft(draft: DraftEntity)

    @Query("SELECT * FROM drafts ORDER BY writtenDate DESC")
    fun getOrderDrafts(): Flow<List<DraftEntity>>

    @Query("SELECT * from drafts")
    suspend fun getAllDrafts(): List<DraftEntity>

    @Query("UPDATE drafts SET draftAnnotation=:notes WHERE title=:title")
    suspend fun updateNoteByTitle(notes: String, title: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDraft(draft: DraftEntity)

    @Delete
    suspend fun deleteDraft(draft: DraftEntity)
}
