package dev.pgm.poembox.data.local.dao

import androidx.room.*
import dev.pgm.poembox.data.local.entities.SheetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SheetDao {
    @Query("SELECT * FROM sheets WHERE id=:id")
    suspend fun findById(id: Int): SheetEntity?

    @Query("SELECT * FROM sheets WHERE dateCreation=:date")
    suspend fun findByDateCreation(date: String): SheetEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSheet(sheet: SheetEntity)

    @Query("SELECT * FROM sheets ORDER BY dateCreation DESC")
    fun getOrderSheet(): Flow<List<SheetEntity>>

    @Query("SELECT * from sheets")
    suspend fun getAllSheet(): List<SheetEntity>

    @Update
    suspend fun updateSheet(sheet: SheetEntity)

    @Delete
    suspend fun deleteSheet(sheet: SheetEntity)
}
