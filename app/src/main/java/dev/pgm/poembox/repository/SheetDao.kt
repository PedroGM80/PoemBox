package dev.pgm.poembox.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SheetDao {
    @Query("SELECT * FROM sheets WHERE id=:id")
    suspend fun findById(id: Int): Sheet

    @Query("SELECT * FROM sheets WHERE dateCreation=:date")
    suspend fun findByDateCreation(date: String): Sheet

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSheet(sheet: Sheet)

    @Query("SELECT * FROM sheets ORDER BY dateCreation DESC")
    fun getOrderSheet(): Flow<List<Sheet>>

    @Query("SELECT * from sheets")
    suspend fun getAllSheet(): MutableList<Sheet>

    @Update
    suspend fun updateSheet(sheet: Sheet)

    @Delete
    suspend fun deleteSheet(sheet: Sheet)

}