package dev.pgm.poembox.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Sheet dao
 *
 * @constructor Create empty Sheet dao
 */
@Dao
interface SheetDao {
    /**
     * Find by id
     *
     * @param id
     * @return  Sheet
     */
    @Query("SELECT * FROM sheets WHERE id=:id")
    suspend fun findById(id: Int): Sheet

    /**
     * Find by date creation
     *
     * @param date
     * @return  Sheet
     */
    @Query("SELECT * FROM sheets WHERE dateCreation=:date")
    suspend fun findByDateCreation(date: String): Sheet

    /**
     * Add sheet
     *
     * @param sheet
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSheet(sheet: Sheet)

    /**
     * Get order sheet
     *
     * @return  Flow<List<Sheet>> order by date creation
     */
    @Query("SELECT * FROM sheets ORDER BY dateCreation DESC")
    fun getOrderSheet(): Flow<List<Sheet>>

    /**
     * Get all sheet
     *
     * @return all sheets
     */
    @Query("SELECT * from sheets")
    suspend fun getAllSheet(): MutableList<Sheet>

    /**
     * Update sheet
     *
     * @param sheet
     */
    @Update
    suspend fun updateSheet(sheet: Sheet)

    /**
     * Delete sheet
     *
     * @param sheet
     */
    @Delete
    suspend fun deleteSheet(sheet: Sheet)

}