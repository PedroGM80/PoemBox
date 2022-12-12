package dev.pgm.poembox.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Draft dao
 *
 * @constructor Create empty Draft dao
 */
@Dao
interface DraftDao {

    /**
     * Find by id
     *
     * @param id
     * @return draft
     */
    @Query("SELECT * FROM drafts WHERE id=:id")
    suspend fun findById(id: Int): Draft

    /**
     * Find by title
     *
     * @param title
     * @return draft
     */
    @Query("SELECT * FROM drafts WHERE title=:title")
    suspend fun findByTitle(title: String): Draft

    /**
     * Add draft
     *
     * @param draft
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDraft(draft: Draft)

    /**
     * Get order drafts
     *
     * @return order drafts
     */
    @Query("SELECT * FROM drafts ORDER BY writtenDate DESC")
    fun getOrderDrafts(): Flow<List<Draft>>

    /**
     * Get all drafts
     *
     * @return drafts
     */
    @Query("SELECT * from drafts")
    suspend fun getAllDrafts(): MutableList<Draft>

    /**
     * Update note by title
     *
     * @param notes
     * @param title
     */
    @Query("UPDATE drafts SET draftAnnotation=:notes WHERE title=:title")
    suspend fun updateNoteByTitle(notes: String, title: String)

    /**
     * Update draft
     *
     * @param draft
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDraft(draft: Draft)

    /**
     * Delete draft
     *
     * @param draft
     */
    @Delete
    suspend fun deleteDraft(draft: Draft)

}