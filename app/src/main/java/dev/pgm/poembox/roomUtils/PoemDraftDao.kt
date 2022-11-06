package dev.pgm.poembox.roomUtils

import androidx.room.*

@Dao
interface PoemDraftDao {
    @Query("SELECT * from PoemDraft")
    suspend fun getAll(): MutableList<PoemDraft>

    @Query("SELECT * FROM PoemDraft WHERE id = :id")
    suspend fun findById(id: Int): PoemDraft

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(draft: MutableList<PoemDraft>)

    @Update
    suspend fun update(draft: PoemDraft)

    @Delete
    suspend fun delete(draft: PoemDraft)
}